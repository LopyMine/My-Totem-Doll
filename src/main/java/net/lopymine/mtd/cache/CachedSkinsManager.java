package net.lopymine.mtd.cache;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.client.command.reload.ReloadAction;
import net.lopymine.mtd.http.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import org.jetbrains.annotations.*;

//? if >=1.20.2 {
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
//?} else {
/*import net.lopymine.mtd.utils.SkinTextures;
import net.lopymine.mtd.utils.SkinTextures.Model;
*///?}

public class CachedSkinsManager {

	public static final String MINECRAFT_NICKNAME_REGEX = "^[a-zA-Z0-9_]{2,16}$";
	public static final SkinTextures DEFAULT_DOLL_TEXTURES = new SkinTextures(Identifier.of("minecraft","textures/entity/player/wide/steve.png"), null, null, null, Model.WIDE, true);

	private static final Map<String, SkinTextures> CACHE = new ConcurrentHashMap<>();
	private static final Set<String> PLAYER_LOADING_SKINS = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private static final int MAX_LOADING_SKINS = 10;
	private static int loadingSkins = 0;
	private static long lastRequestTime = 0L;

	private static CompletableFuture<Void> loadSkin(String nickname, boolean skipMaxRequestsCheck) {
		if (!skipMaxRequestsCheck) {
			long now = System.currentTimeMillis();
			if (now - lastRequestTime > 1000) {
				loadingSkins    = 0;
				lastRequestTime = now;
			}
			if (loadingSkins >= MAX_LOADING_SKINS) {
				return CompletableFuture.completedFuture(null);
			}
			loadingSkins++;

			if (!PLAYER_LOADING_SKINS.add(nickname)) {
				return CompletableFuture.completedFuture(null);
			}
		}

		return CompletableFuture.supplyAsync(() -> {
			try {
				long waitMs = 1000;

				while (true) {
					Response<SkinTextures> response = MojangAPI.getSkinTextures(nickname);
					if (response.isEmpty() && response.statusCode() == 429) {
						Thread.sleep(waitMs);
						waitMs += 500;
					} else if (!response.isEmpty()) {
						SkinTextures skinTextures = response.value();
						CACHE.put(nickname.toLowerCase(), skinTextures);
						break;
					} else {
						break;
					}
				}
			} catch (Exception e) {
				MyTotemDollClient.LOGGER.error("Failed to load skin for {}:", nickname, e);
			} finally {
				PLAYER_LOADING_SKINS.remove(nickname);
			}
			return null;
		});
	}

	@NotNull
	public static SkinTextures getSkin(@NotNull String nickname) {
		if (!nickname.matches(MINECRAFT_NICKNAME_REGEX)) {
			return DEFAULT_DOLL_TEXTURES;
		}
		SkinTextures skinTextures = CACHE.get(nickname.toLowerCase());
		if (skinTextures == null) {
			CachedSkinsManager.loadSkin(nickname, false);
			return DEFAULT_DOLL_TEXTURES;
		}
		return skinTextures;
	}

	public static Set<String> getLoadedPlayers() {
		return CACHE.keySet();
	}

	public static void reload(ReloadAction action) {
		Set<CompletableFuture<?>> list = new HashSet<>();

		long startMs = System.currentTimeMillis();
		Set<Entry<String, SkinTextures>> entries = CACHE.entrySet();
		for (Entry<String, SkinTextures> entry : entries) {
			String nickname = entry.getKey();
			SkinTextures skinTextures = entry.getValue();
			CACHE.put(nickname.toLowerCase(), DEFAULT_DOLL_TEXTURES);
			destroyTexture(skinTextures);
			CACHE.remove(nickname);

			list.add(loadSkin(nickname, true));
		}

		CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenApply((__) -> {
			action.action((System.currentTimeMillis() - startMs) / 1000F);
			return null;
		});
	}

	private static void destroyTexture(@Nullable SkinTextures skinTextures) {
		if (skinTextures != null) {
			if (skinTextures.equals(DEFAULT_DOLL_TEXTURES)) {
				return;
			}
			TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

			textureManager.destroyTexture(skinTextures.texture());
			if (skinTextures.capeTexture() != null) {
				textureManager.destroyTexture(skinTextures.capeTexture());
			}
		}
	}

	public static void reload(String nickname, ReloadAction action) {
		long startMs = System.currentTimeMillis();

		CompletableFuture.runAsync(() -> {
			SkinTextures skinTextures = CACHE.get(nickname);
			CACHE.put(nickname.toLowerCase(), DEFAULT_DOLL_TEXTURES);

			destroyTexture(skinTextures);

			loadSkin(nickname, true).thenApply((__) -> {
				action.action((System.currentTimeMillis() - startMs) / 1000F);
				return null;
			});
		});
	}
}
