package net.lopymine.mtd.atlas.manager;

import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.util.collection.*;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.totem.TotemDollArmsType;
import net.lopymine.mtd.doll.data.TotemDollSprites;
import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasSpriteManager {

	private static final LongObjectMap<AtlasSprite> CACHED_SPECIAL_REMAPPED_SPRITES = new LongObjectHashMap<>();
	private static final LongObjectMap<AtlasSprite> CACHED_SPECIAL_SKIN_SPRITES = new LongObjectHashMap<>();

	private static final List<AtlasSprite> ATLAS_SPRITES = new ArrayList<>();

	public static List<AtlasSprite> getSprites() {
		return ATLAS_SPRITES;
	}

	public static void registerSprite(AtlasSprite id, boolean stitchAndUpdate, @Nullable OnAtlasStitched onAtlasStitched) {
		loadFromResource(id.getSpriteId(), (image) -> registerSprite(id, image, stitchAndUpdate, onAtlasStitched));
	}

	public static void registerSprite(AtlasSprite sprite, NativeImage image, boolean stitchAndUpdate, @Nullable OnAtlasStitched onAtlasStitched) {
		AtlasSprite.updateContents(sprite, image);
		ATLAS_SPRITES.add(sprite);
		if (stitchAndUpdate) {
			MyTotemDollAtlasManager.stitchAndUpdate(ATLAS_SPRITES, onAtlasStitched);
		}
	}

	public static void registerSpecialSkinSprite(Identifier id, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		loadFromResource(id, (image) -> registerSpecialSkinSprite(id, image, stitchAndUpdate, onSpriteUploaded));
	}

	public static void registerSpecialSkinSprite(Identifier id, NativeImage image, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		registerSpecialSprite(image, id, CACHED_SPECIAL_SKIN_SPRITES, stitchAndUpdate, onSpriteUploaded);
	}

	public static void registerSpecialRemappedSprite(AtlasSprite sprite) {
		long cachedId = sprite.getCachedId();
		if (cachedId != -1 && CACHED_SPECIAL_REMAPPED_SPRITES.containsKey(cachedId)) {
			return;
		}

		Identifier resourceId = sprite.getSpriteId();
		sprite.setSpriteId(MyTotemDoll.id("remapped_sprites/%s.png".formatted(MathHelper.abs(resourceId.toString().hashCode()))));

		loadFromResource(resourceId, (image) -> {
			NativeImage remapped = PlayerSkinUtils.remapTextureToStandardSize(image, true);
			registerSpecialSprite(remapped, resourceId, CACHED_SPECIAL_REMAPPED_SPRITES, false, sprite::copyFrom);
		});
	}

	private static void registerSpecialSprite(NativeImage image, Identifier id, LongObjectMap<AtlasSprite> specialSprites, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		long spriteUniqueId = AtlasSprite.generateUniqueIdByContent(image);
		AtlasSprite alreadyRegisteredSprite = specialSprites.get(spriteUniqueId);
		if (alreadyRegisteredSprite != null) {
			if (onSpriteUploaded != null) {
				onSpriteUploaded.onUploaded(alreadyRegisteredSprite);
			}
			image.close();
			return;
		}

		AtlasSprite sprite = AtlasSprite.of(id, image);

		sprite.setCachedId(spriteUniqueId);
		sprite.setClosable(false);
		sprite.setUnregisterAction(() -> {
			specialSprites.remove(spriteUniqueId);
			ATLAS_SPRITES.remove(sprite);
		});
		AtlasSprite oldValue = specialSprites.put(spriteUniqueId, sprite);
		if (oldValue != null) {
			oldValue.closeAnyway();
			ATLAS_SPRITES.remove(oldValue);
		}
		ATLAS_SPRITES.add(sprite);

		if (stitchAndUpdate && onSpriteUploaded != null) {
			MyTotemDollAtlasManager.stitchAndUpdate(ATLAS_SPRITES, () -> onSpriteUploaded.onUploaded(sprite));
		} else {
			sprite.setUploadAction(onSpriteUploaded);
		}
	}


	private static void loadFromResource(Identifier id, Consumer<NativeImage> consumer) {
		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).orElse(null);
		if (resource == null) {
			AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().textures.get(id);
			if (!(texture instanceof NativeImageBackedTexture backedTexture)) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to find texture even from TextureManager!");
				return;
			}
			NativeImage image = backedTexture.getImage();
			if (image == null) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Found image in TextureManager, but it's null somehow!?");
				return;
			}
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
		ATLAS_SPRITES.forEach(AtlasSprite::closeAnyway);
	}

	public static void reload() {
		ATLAS_SPRITES.forEach(AtlasSprite::close);
		ATLAS_SPRITES.clear();
		ATLAS_SPRITES.add(AtlasSprite.of(MissingSprite.createSpriteContents()));
		ATLAS_SPRITES.addAll(CACHED_SPECIAL_SKIN_SPRITES.values());
		ATLAS_SPRITES.addAll(CACHED_SPECIAL_REMAPPED_SPRITES.values());
		registerSprite(TotemDollSprites.STEVE_SKIN_SPRITE, false, null);
		registerSpecialRemappedSprite(TotemDollSprites.ELYTRA_SPRITE);
	}
}
