package net.lopymine.mtd.atlas.manager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.texture.SpriteLoader.Preparations;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasManager {

	public static final Identifier ATLAS_ID = MyTotemDoll.id("main_atlas.png");
	public static final RenderType ATLAS_RENDER_LAYER = RenderTypes.entityTranslucent(ATLAS_ID);
	private static final StitchHooksManager STITCH_HOOKS_MANAGER = new StitchHooksManager();
	private static final AtomicInteger LATEST_ATLAS_VERSION = new AtomicInteger();
	@Nullable
	private static LockableAtlasTexture ATLAS_TEXTURE;

	@NotNull
	public static TextureAtlas createNotRegisteredInstance() {
		return new TextureAtlas(ATLAS_ID);
	}

	public static RenderType getRenderLayer() {
		return ATLAS_RENDER_LAYER;
	}

	public static LockableAtlasTexture getNullableAtlasTexture() {
		return ATLAS_TEXTURE;
	}

	public static void setAtlas(@NotNull TextureAtlas texture) {
		if (ATLAS_TEXTURE != null && ATLAS_TEXTURE.isLocked()) {
			LockableAtlasTexture atlasTexture = new LockableAtlasTexture(texture);
			ATLAS_TEXTURE.setUnlockHook(() -> set(atlasTexture));
			return;
		}


		set(new LockableAtlasTexture(texture));
	}

	@NotNull
	private static LockableAtlasTexture set(@NotNull LockableAtlasTexture texture) {
		TextureAtlas atlas = texture.getAtlas();
		ATLAS_TEXTURE = texture;
		Minecraft.getInstance().getTextureManager().register(atlas.location(), atlas);
		return ATLAS_TEXTURE;
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, MyTotemDollTaskExecutor.MAIN_EXECUTOR, onAtlasStitched);
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, Executor executor, @Nullable OnAtlasStitched onAtlasStitched) {
		stitchAndUpdate(sprites, null, executor, Minecraft.getInstance(), onAtlasStitched);
	}

	public static void stitchAndUpdate(Set<AtlasSprite> sprites, @Nullable PreparableReloadListener.PreparationBarrier synchronizer, Executor prepareExecutor, Executor applyExecutor, @Nullable OnAtlasStitched onAtlasStitched) {
		int currentId = LATEST_ATLAS_VERSION.incrementAndGet();
		STITCH_HOOKS_MANAGER.addHook(onAtlasStitched);

		TextureAtlas atlasTexture = MyTotemDollAtlasManager.createNotRegisteredInstance();

		List<SpriteContents> contents = sprites.stream().map(AtlasSprite::getContents).filter(Objects::nonNull).toList();

		CompletableFuture<Preparations> future = CompletableFuture.supplyAsync(
				() -> SpriteLoader.create(atlasTexture).stitch(contents, 0, prepareExecutor)
		);

		if (synchronizer != null) {
			future = future.thenCompose(synchronizer::wait);
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

	private record AtlasStitchingContext(int version, TextureAtlas atlas, Set<AtlasSprite> atlasSprites) {

		public void upload(Preparations result) {
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
