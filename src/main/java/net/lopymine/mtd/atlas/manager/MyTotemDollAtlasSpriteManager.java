package net.lopymine.mtd.atlas.manager;

import io.netty.util.collection.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.TotemDollSprites;
import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasSpriteManager {

	private static final Object LOCK = new Object();

	private static final LongObjectMap<AtlasSprite> CONTENT_CACHED_SPECIAL_SKIN_SPRITES = new LongObjectHashMap<>();
	private static final LongObjectMap<AtlasSprite> REMAPPED_SPRITES = new LongObjectHashMap<>();
	private static final Map<Identifier, AtlasSprite> DYNAMIC_SPRITES = new HashMap<>();

	private static final List<AtlasSprite> ATLAS_SPRITES = new ArrayList<>();

	public static List<AtlasSprite> getSprites() {
		synchronized (LOCK) {
			return new ArrayList<>(ATLAS_SPRITES);
		}
	}

	public static void registerDynamicSprite(AtlasSprite id, boolean stitchAndUpdate, @Nullable OnAtlasStitched onAtlasStitched) {
		loadFromResource(id.getSpriteId(), (image) -> registerDynamicSprite(id, image, stitchAndUpdate, onAtlasStitched));
	}

	public static void registerDynamicSprite(AtlasSprite sprite, NativeImage image, boolean stitchAndUpdate, @Nullable OnAtlasStitched onAtlasStitched) {
		AtlasSprite.updateContents(sprite, image);

		synchronized (LOCK) {
			AtlasSprite oldValue = DYNAMIC_SPRITES.put(sprite.getSpriteId(), sprite);
			if (oldValue != null && oldValue != sprite) {
				oldValue.closeAnyway();
				ATLAS_SPRITES.remove(oldValue);
			}

			ATLAS_SPRITES.add(sprite);
		}

		if (stitchAndUpdate) {
			MyTotemDollAtlasManager.stitchAndUpdate(getSprites(), onAtlasStitched);
		}
	}

	public static void registerSpecialSkinSprite(Identifier id, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		loadFromResource(id, (image) -> registerSpecialSkinSprite(id, image, stitchAndUpdate, onSpriteUploaded));
	}

	public static void registerSpecialSkinSprite(Identifier id, NativeImage image, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		registerSpecialContentCachedSprite(image, id, CONTENT_CACHED_SPECIAL_SKIN_SPRITES, AtlasSprite::of, stitchAndUpdate, onSpriteUploaded);
	}

	public static void registerSpecialRemappedSprite(RemappedAtlasSprite sprite) {
		registerSpecialRemappedSprite(sprite, true);
	}

	public static void registerSpecialRemappedSprite(RemappedAtlasSprite sprite, boolean checkIfCached) {
		if (checkIfCached) {
			synchronized (LOCK) {
				long cachedId = sprite.getCachedId();
				if (cachedId != -1 && REMAPPED_SPRITES.containsKey(cachedId)) {
					return;
				}
			}
		}

		Identifier resourceId = sprite.getResourceId();
		loadFromResource(resourceId, (image) -> {
			NativeImage remapped = PlayerSkinUtils.remapTextureToStandardSize(image, true);
			registerSpecialContentCachedSprite(remapped, resourceId, REMAPPED_SPRITES, RemappedAtlasSprite::ofResource, false, sprite::copyFrom);
		});
	}

	private static void registerSpecialContentCachedSprite(NativeImage image, Identifier id, LongObjectMap<AtlasSprite> specialSprites, BiFunction<Identifier, NativeImage, AtlasSprite> spriteFactory, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		long spriteUniqueId = AtlasSprite.generateUniqueIdByContent(image);
		AtlasSprite alreadyRegisteredSprite = specialSprites.get(spriteUniqueId);
		if (alreadyRegisteredSprite != null) {
			if (onSpriteUploaded != null) {
				onSpriteUploaded.onUploaded(alreadyRegisteredSprite);
			}
			image.close();
			return;
		}

		AtlasSprite sprite = spriteFactory.apply(id, image);

		sprite.setCachedId(spriteUniqueId);
		sprite.setClosable(false);
		sprite.setUnregisterAction(() -> {
			synchronized (LOCK) {
				specialSprites.remove(spriteUniqueId);
				ATLAS_SPRITES.remove(sprite);
			}
		});

		synchronized (LOCK) {
			AtlasSprite oldValue = specialSprites.put(spriteUniqueId, sprite);
			if (oldValue != null && oldValue != sprite) {
				oldValue.closeAnyway();
				ATLAS_SPRITES.remove(oldValue);
			}
			ATLAS_SPRITES.add(sprite);
		}

		if (stitchAndUpdate && onSpriteUploaded != null) {
			MyTotemDollAtlasManager.stitchAndUpdate(getSprites(), () -> onSpriteUploaded.onUploaded(sprite));
		} else {
			sprite.setUploadAction(onSpriteUploaded);
		}
	}

	private static void loadFromResource(Identifier id, Consumer<NativeImage> consumer) {
		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).orElse(null);
		if (resource == null) {
			AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().textures.get(id);
			//? if >=1.21 {
			if (!(texture instanceof NativeImageBackedTexture backedTexture)) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to find texture even from TextureManager! Id: \"{}\", Texture Class: \"{}\"", id, texture == null ? "null" : texture.getClass().getSimpleName());
				return;
			}
			NativeImage image = backedTexture.getImage();
			if (image == null) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Found image in TextureManager, but it's null somehow!? Id: \"{}\"", id);
				return;
			}
			//?} else {
			/*NativeImage image = null;

			if (texture instanceof PlayerSkinTexture playerSkinTexture) {
				File cacheFile = playerSkinTexture.cacheFile;
				if (cacheFile != null && cacheFile.exists()) {
					try (FileInputStream stream = new FileInputStream(cacheFile)) {
						image = NativeImage.read(stream);
					} catch (Exception e) {
						MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to read player skin texture from cache, id: \"{}\", folder: \"{}\"", id, cacheFile);
					}
				} else {
					String url = playerSkinTexture.url;
					try {
						image = PlayerSkinUtils.remapSkinTexture(PlayerSkinUtils.download(url));
					} catch (Exception e) {
						MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to download player skin texture from url, id: \"{}\", url: \"{}\"", id, url);
					}
				}
			}

			if (image == null) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to find texture even from TextureManager! Id: \"{}\", Texture Class: \"{}\"", id, texture == null ? "null" : texture.getClass().getSimpleName());
				return;
			}
			*///?}

			NativeImage nativeImage = new NativeImage(image.getWidth(), image.getWidth(), true);
			nativeImage.copyFrom(image);
			consumer.accept(nativeImage);
			return;
		}
		try {
			consumer.accept(NativeImage.read(resource.getInputStream()));
		} catch (IOException e) {
			MyTotemDollClient.LOGGER.error("Failed to load resource for mod's atlas:", e);
		}
	}

	public static void close() {
		synchronized (LOCK) {
			ATLAS_SPRITES.forEach(AtlasSprite::closeAnyway);
		}
	}

	public static void reload() {
		synchronized (LOCK) { // todo make here "special" lock stuff
			ATLAS_SPRITES.forEach(AtlasSprite::close);
			ATLAS_SPRITES.clear();
			ATLAS_SPRITES.add(AtlasSprite.of(MissingSprite.createSpriteContents()));
			ATLAS_SPRITES.addAll(CONTENT_CACHED_SPECIAL_SKIN_SPRITES.values());
			ATLAS_SPRITES.addAll(REMAPPED_SPRITES.values());
			DYNAMIC_SPRITES.clear();
		}
		registerDynamicSprite(TotemDollSprites.STEVE_SKIN_SPRITE, false, null);
		registerSpecialRemappedSprite(TotemDollSprites.ELYTRA_SPRITE, false);
	}
}
