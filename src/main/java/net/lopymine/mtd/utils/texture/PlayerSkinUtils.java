package net.lopymine.mtd.utils.texture;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.net.*;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasSpriteManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.*;

public class PlayerSkinUtils {

	public static void downloadSkin(@NotNull String textureUrl, @NotNull Identifier textureId, @Nullable SuccessAction onSuccessRegistration, @Nullable FailedAction onFailedRegistration, boolean skin) {
		try {
			NativeImage nativeImage = download(textureUrl);
			NativeImage image = skin ? remapSkinTexture(nativeImage) : remapTextureToStandardSize(nativeImage, true);
			MyTotemDollAtlasSpriteManager.registerSpecialSkinSprite(textureId, image, true, (sprite) -> {
				if (onSuccessRegistration != null) {
					onSuccessRegistration.onSuccess(sprite);
				}
			});
		} catch (Exception e) {
			MyTotemDollClient.LOGGER.error("Failed to download skin texture with id \"{}\": ", textureId, e);
			if (onFailedRegistration != null) {
				onFailedRegistration.onFailed(e);
			}
		}
	}

	public static NativeImage download(String uri) throws IOException {
		HttpURLConnection connection = null;
		MyTotemDollClient.LOGGER.debug("Downloading HTTP texture from {}", uri);
		URI currentUri = URI.create(uri);

		NativeImage image;
		try {
			connection = (HttpURLConnection) currentUri.toURL().openConnection(Minecraft.getInstance().getProxy());
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.connect();
			int i = connection.getResponseCode();
			if (i / 100 != 2) {
				String url = String.valueOf(currentUri);
				throw new IOException("Failed to open " + url + ", HTTP error code: " + i);
			}

			image = NativeImage.read(connection.getInputStream().readAllBytes());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}

		}
		return image;
	}

	public static @NotNull NativeImage remapTextureToStandardSize(NativeImage image, boolean closeOriginal) {
		if (image.getWidth() == 64 && image.getHeight() == 64) {
			return image;
		}
		NativeImage nativeImage = new NativeImage(64, 64, true);
		nativeImage.copyFrom(image);
		if (closeOriginal) {
			image.close();
		}
		return nativeImage;
	}

	public static NativeImage remapSkinTexture(NativeImage image) {
		int i = image.getHeight();
		boolean bl = i == 32;
		if (bl) {
			NativeImage nativeImage = new NativeImage(64, 64, true);
			nativeImage.copyFrom(image);
			image.close();
			image = nativeImage;
			nativeImage.fillRect(0, 32, 64, 32, 0);
			nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
			nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
			nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
			nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
			nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
			nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
			nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
			nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
			nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
			nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
			nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
			nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
		}

		stripAlpha(image, 0, 0, 32, 16);
		if (bl) {
			stripColor(image, 32, 0, 64, 32);
		}

		stripAlpha(image, 0, 16, 64, 32);
		stripAlpha(image, 16, 48, 48, 64);
		return image;
	}

	@SuppressWarnings("all")
	private static void stripColor(NativeImage image, int x1, int y1, int x2, int y2) {
		for (int i = x1; i < x2; ++i) {
			for (int j = y1; j < y2; ++j) {
				int k = image.getPixel(i, j);
				if (ARGB.alpha(k) < 128) {
					return;
				}
			}
		}

		for (int i = x1; i < x2; ++i) {
			for (int j = y1; j < y2; ++j) {
				image.setPixel(i, j, image.getPixel(i, j) & 16777215);
			}
		}
	}

	@SuppressWarnings("all")
	private static void stripAlpha(NativeImage image, int x1, int y1, int x2, int y2) {
		for (int i = x1; i < x2; ++i) {
			for (int j = y1; j < y2; ++j) {
				image.setPixel(i, j, ARGB.opaque(image.getPixel(i, j)));
			}
		}
	}


	public static void setupClientTextures(TotemDollData data) {
		Minecraft.getInstance().getSkinManager().get(Minecraft.getInstance().getGameProfile()).thenAccept((optional) -> {
			if (optional.isEmpty()) {
				return;
			}
			PlayerSkin skinTextures = optional.get();
			data.setSprites(TotemDollSprites.of(skinTextures));
		});
	}
}
