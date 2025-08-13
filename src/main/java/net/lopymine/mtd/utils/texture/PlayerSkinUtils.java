package net.lopymine.mtd.utils.texture;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.manager.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.PlayerSkinTextureExtension;

import java.io.*;
import java.nio.file.*;
import java.util.function.Supplier;
import org.jetbrains.annotations.*;

//? <=1.21.3 {
/*import net.minecraft.client.util.DefaultSkinHelper;
*///?}

@ExtensionMethod(PlayerSkinTextureExtension.class)
public class PlayerSkinUtils {

	public static void downloadSkin(@NotNull String textureUrl, @NotNull Identifier textureId, @Nullable SuccessAction onSuccessRegistration, @Nullable FailedAction onFailedRegistration, boolean cape, Path cachedTexturePath) {
		try {
			Supplier<NativeImage> supplier = PlayerSkinUtils.download(cachedTexturePath, textureUrl, cape, textureId); // DO NOT CLOSE

			MinecraftClient.getInstance().send(() -> {
				NativeImage image = supplier.get();

				//? <=1.21.3 {
				/*if (image instanceof PlayerSkinTexture playerSkinTexture) {
					playerSkinTexture.setOnSuccessAction(onSuccessRegistration);
					playerSkinTexture.setOnFailedAction(onFailedRegistration);
					if (cape) {
						playerSkinTexture.markAsCape();
					}
				}
				*///?}

				MyTotemDollAtlasSpriteManager.registerSpecialSkinSprite(textureId, image, true, (sprite) -> {
					if (onSuccessRegistration != null) {
						onSuccessRegistration.onSuccess(sprite);
					}
				});
			});
		} catch (Exception e) {
			MyTotemDollClient.LOGGER.error("Failed to download skin texture with id \"%s\": ".formatted(textureId), e.getMessage());
			if (onFailedRegistration != null) {
				onFailedRegistration.onFailed(e.getMessage(), e);
			}
		}
	}

	private static Supplier<NativeImage> download(Path path, String uri, boolean cape, Identifier id) throws IOException {
		//? >=1.21.4 {
		NativeImage download = PlayerSkinTextureDownloader.download(path, uri);
		if (cape) {
			download = remapTextureToStandardSize(download, true);
		} else {
			download = PlayerSkinTextureDownloader.remapTexture(download, uri);
		}
		NativeImage finalDownload = download;
		return () -> finalDownload;
		//?} else {
		/*return () -> new PlayerSkinTexture(path.toFile(), uri, DefaultSkinHelper.getTexture(), cape, () -> {
			try {
				if (path.toFile().exists()) {
					Files.delete(path);
				}
			} catch (FileSystemException ignored) {
			} catch (FileNotFoundException e) {
				if (MyTotemDollClient.getConfig().isDebugLogEnabled()) {
					MyTotemDollClient.LOGGER.warn("Failed to find temp texture file at {} to delete it", path);
				}
			} catch (Exception e) {
				if (MyTotemDollClient.getConfig().isDebugLogEnabled()) {
					MyTotemDollClient.LOGGER.error("Failed to delete temp texture file: ", e);
				}
			}
		});
		*///?}
	}

	public static @NotNull NativeImage remapTextureToStandardSize(NativeImage image, boolean close) {
		NativeImage nativeImage = new NativeImage(64, 64, true);
		nativeImage.copyFrom(image);
		if (close) {
			image.close();
		}
		return nativeImage;
	}
}
