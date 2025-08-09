package net.lopymine.mtd.atlas.manager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasManager {

	private static final StitchHooksManager STITCH_HOOKS_MANAGER = new StitchHooksManager();
	private static final AtomicInteger LATEST_ATLAS_VERSION = new AtomicInteger();
	@Nullable
	private static LockableAtlasTexture ATLAS_TEXTURE;

	@NotNull
	public static SpriteAtlasTexture createNotRegisteredInstance() {
		return new SpriteAtlasTexture(MyTotemDoll.id("main_atlas.png"));
	}

	@NotNull
	public static LockableAtlasTexture getAtlasTexture() {
		if (ATLAS_TEXTURE == null) {
			return setAtlas(createNotRegisteredInstance());
		}
		return ATLAS_TEXTURE;
	}

	@NotNull
	public static LockableAtlasTexture setAtlas(@NotNull SpriteAtlasTexture texture) {
		if (ATLAS_TEXTURE != null && ATLAS_TEXTURE.isLocked()) {
			LockableAtlasTexture atlasTexture = new LockableAtlasTexture(texture);
			ATLAS_TEXTURE.setUnlockHook(() -> set(atlasTexture));
			return atlasTexture;
		}
		return set(new LockableAtlasTexture(texture));
	}

	@NotNull
	private static LockableAtlasTexture set(@NotNull LockableAtlasTexture texture) {
		SpriteAtlasTexture atlas = texture.getAtlas();
		ATLAS_TEXTURE = texture;
		MinecraftClient.getInstance().getTextureManager().registerTexture(atlas.getId(), atlas);
		return ATLAS_TEXTURE;
	}

	@NotNull
	public static Sprite getSprite(Identifier id) {
		return getAtlasTexture().getAtlas().getSprite(id);
	}

	public static void stitchAndUpdate(List<AtlasSprite> sprites, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, MyTotemDollTaskExecutor.MAIN_EXECUTOR, onAtlasStitched);
	}

	public static void stitchAndUpdate(List<AtlasSprite> sprites, Executor executor, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, null, executor, MinecraftClient.getInstance(), onAtlasStitched);
	}

	public static void stitchAndUpdate(List<AtlasSprite> sprites, @Nullable ResourceReloader.Synchronizer synchronizer, Executor prepareExecutor, Executor applyExecutor, @Nullable OnAtlasStitched onAtlasStitched) {
		int currentId = LATEST_ATLAS_VERSION.incrementAndGet();
		STITCH_HOOKS_MANAGER.addHook(onAtlasStitched);

		SpriteAtlasTexture atlasTexture = MyTotemDollAtlasManager.createNotRegisteredInstance();

		List<SpriteContents> contents = sprites.stream().map(AtlasSprite::getContents).filter(Objects::nonNull).toList();
		CompletableFuture<StitchResult> future = SpriteLoader.fromAtlas(atlasTexture)
				.stitch(contents, 0, prepareExecutor)
				.whenComplete();

		if (synchronizer != null) {
			future = future.thenCompose(synchronizer::whenPrepared);
		}

		AtlasStitchingContext stitchingContext = new AtlasStitchingContext(currentId, atlasTexture, sprites);
		future.thenAcceptAsync(stitchingContext::upload, applyExecutor);
	}

	private record AtlasStitchingContext(int version, SpriteAtlasTexture atlas, List<AtlasSprite> atlasSprites) {

		public void upload(StitchResult result) {
			int latestAtlasVersion = LATEST_ATLAS_VERSION.get();
			if (this.version != latestAtlasVersion) {
				MyTotemDollClient.LOGGER.warn("Skipped atlas stitching, waiting \"{}\"", latestAtlasVersion);
				return;
			}
			this.atlas.upload(result);
			this.atlasSprites.forEach(AtlasSprite::markUploaded);
			MyTotemDollAtlasManager.setAtlas(this.atlas);
			STITCH_HOOKS_MANAGER.runAllHooks();
		}

	}

}
