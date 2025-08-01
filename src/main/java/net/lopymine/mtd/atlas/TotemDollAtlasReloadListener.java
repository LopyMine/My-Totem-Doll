package net.lopymine.mtd.atlas;

import java.util.*;
import java.util.concurrent.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.SpriteLoader.StitchResult;
import net.minecraft.resource.ResourceReloader.Synchronizer;
import org.jetbrains.annotations.Nullable;

public class TotemDollAtlasReloadListener {

	public static void reloadAtlas(List<SpriteContents> contents, Executor executor) {
		reloadAtlas(contents, executor, null);
	}

	public static void reloadAtlas(List<SpriteContents> contents, Executor executor, @Nullable Synchronizer synchronizer) {
		SpriteAtlasTexture atlasTexture = MyTotemDollAtlasManager.getNewInstance();
		SpriteLoader loader = SpriteLoader.fromAtlas(atlasTexture);
		CompletableFuture<StitchResult> future = loader.stitch(contents, 0, executor).whenComplete();
		if (synchronizer != null) {
			future = future.thenCompose(synchronizer::whenPrepared);
		}
		future.thenAcceptAsync((result) -> {
			atlasTexture.upload(result);
			MyTotemDollAtlasManager.setAtlas(atlasTexture);
		}, MinecraftClient.getInstance());
	}
}
