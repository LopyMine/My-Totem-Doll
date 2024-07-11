package net.lopymine.mtd.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import net.fabricmc.loader.api.FabricLoader;

import net.lopymine.mtd.client.MyTotemDollClient;

import java.io.*;
import java.nio.file.*;
import org.jetbrains.annotations.*;

public class TextureUtils {

	public static void registerUrlTexture(@NotNull String textureUrl, @NotNull Identifier textureId, @Nullable Path texturePath, boolean removeAfterReg) {
		Path cachedTexturePath = TextureUtils.getCachedTexturePath(texturePath, textureUrl);
		if (cachedTexturePath == null) {
			return;
		}
		PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(cachedTexturePath.toFile(), textureUrl, DefaultSkinHelper.getTexture(), true, () -> {
			if (!removeAfterReg) {
				return;
			}
			try {
				if (cachedTexturePath.toFile().exists()) {
					Files.delete(cachedTexturePath);
				}
			} catch (FileSystemException ignored) {
			} catch (FileNotFoundException e) {
				if (MyTotemDollClient.getConfig().isDebugLogEnabled()) {
					MyTotemDollClient.LOGGER.warn("Failed to find temp texture file at {} to delete it", cachedTexturePath);
				}
			} catch (Exception e) {
				if (MyTotemDollClient.getConfig().isDebugLogEnabled()) {
					MyTotemDollClient.LOGGER.error("Failed to delete temp texture: ", e);
				}
			}
		});
		MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, playerSkinTexture);
	}

	private static @Nullable Path getCachedTexturePath(@Nullable Path texturePath, String textureUrl) {
		if (texturePath != null) {
			return texturePath;
		}
		Path defCacheFolder = FabricLoader.getInstance().getGameDir().resolve(".cache");
		File cacheFolderFile = defCacheFolder.toFile();
		if (!cacheFolderFile.exists() && !cacheFolderFile.mkdirs()) {
			return null;
		}
		return defCacheFolder.resolve(String.format("%s.png", Math.abs(textureUrl.hashCode())));
	}
}
