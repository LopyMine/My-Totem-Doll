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
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasManager {

	private static final StitchHooksManager STITCH_HOOKS_MANAGER = new StitchHooksManager();
	private static final AtomicInteger LATEST_ATLAS_VERSION = new AtomicInteger();
	public static final Identifier ATLAS_ID = MyTotemDoll.id("main_atlas.png");
	//? if >=1.21.11 {
	public static final RenderLayer ATLAS_RENDER_LAYER = RenderLayers.entityTranslucent(ATLAS_ID);
	//?} else {
	/*public static final RenderLayer ATLAS_RENDER_LAYER = RenderLayer.getEntityTranslucent(ATLAS_ID);
	*///?}
	@Nullable
	private static LockableAtlasTexture ATLAS_TEXTURE;

	@NotNull
	public static SpriteAtlasTexture createNotRegisteredInstance() {
		return new SpriteAtlasTexture(ATLAS_ID);
	}

	public static RenderLayer getRenderLayer() {
		return ATLAS_RENDER_LAYER;
	}

	public static LockableAtlasTexture getNullableAtlasTexture() {
		return ATLAS_TEXTURE;
	}

	public static void setAtlas(@NotNull SpriteAtlasTexture texture) {
		if (ATLAS_TEXTURE != null && ATLAS_TEXTURE.isLocked()) {
			LockableAtlasTexture atlasTexture = new LockableAtlasTexture(texture);
			ATLAS_TEXTURE.setUnlockHook(() -> set(atlasTexture));
			return;
		}
		set(new LockableAtlasTexture(texture));
	}

	@NotNull
	private static LockableAtlasTexture set(@NotNull LockableAtlasTexture texture) {
		SpriteAtlasTexture atlas = texture.getAtlas();
		ATLAS_TEXTURE = texture;
		MinecraftClient.getInstance().getTextureManager().registerTexture(atlas.getId(), atlas);
		return ATLAS_TEXTURE;
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, MyTotemDollTaskExecutor.MAIN_EXECUTOR, onAtlasStitched);
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, Executor executor, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, null, executor, MinecraftClient.getInstance(), onAtlasStitched);
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, @Nullable ResourceReloader.Synchronizer synchronizer, Executor prepareExecutor, Executor applyExecutor, @Nullable OnAtlasStitched onAtlasStitched) {
		int currentId = LATEST_ATLAS_VERSION.incrementAndGet();
		STITCH_HOOKS_MANAGER.addHook(onAtlasStitched);

		SpriteAtlasTexture atlasTexture = MyTotemDollAtlasManager.createNotRegisteredInstance();

		List<SpriteContents> contents = sprites.stream().map(AtlasSprite::getContents).filter(Objects::nonNull).toList();
		//? if >=1.21.9 {
		CompletableFuture<StitchResult> future = CompletableFuture.supplyAsync(
				() -> SpriteLoader.fromAtlas(atlasTexture).stitch(contents, 0, prepareExecutor)
		);
		//?} else {
		/*CompletableFuture<StitchResult> future = SpriteLoader.fromAtlas(atlasTexture)
				.stitch(contents, 0, prepareExecutor)
				.whenComplete();
		*///?}

		if (synchronizer != null) {
			future = future.thenCompose(synchronizer::whenPrepared);
		}

		AtlasStitchingContext stitchingContext = new AtlasStitchingContext(currentId, atlasTexture, sprites);
		future.thenAcceptAsync(stitchingContext::upload, applyExecutor);
	}

	public static void close() {
		if (ATLAS_TEXTURE == null) {
			return;
		}
		ATLAS_TEXTURE.getAtlas().close();
	}

	private record AtlasStitchingContext(int version, SpriteAtlasTexture atlas, Set<AtlasSprite> atlasSprites) {

		public void upload(StitchResult result) {
			int latestAtlasVersion = LATEST_ATLAS_VERSION.get();
			if (this.version != latestAtlasVersion) {
				MyTotemDollClient.LOGGER.warn("Skipped atlas stitching, waiting \"{}\"", latestAtlasVersion);
				return;
			}
			//? if >=1.21.11 {
			this.atlas.create(result);
			//?} else {
			/*this.atlas.upload(result);
			*///?}
			this.atlasSprites.forEach(AtlasSprite::markUploaded);
			MyTotemDollAtlasManager.setAtlas(this.atlas);
			STITCH_HOOKS_MANAGER.runAllHooks();
 		}

	}

}
