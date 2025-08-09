package net.lopymine.mtd.atlas.manager;

import io.netty.util.collection.*;
import java.io.IOException;
import java.util.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.TotemDollSprites;
import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
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

	public static void registerSprite(AtlasSprite id, @Nullable OnAtlasStitched onAtlasStitched) {
		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id.getSpriteId()).orElse(null);
		if (resource == null) {
			return;
		}
		try {
			registerSprite(id, NativeImage.read(resource.getInputStream()), onAtlasStitched); // Don't close. Thanks.
		} catch (IOException e) {
			MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas!", e);
		}
	}

	public static void registerSprite(AtlasSprite id, NativeImage image, @Nullable OnAtlasStitched onAtlasStitched) {
		SpriteDimensions dimensions = new SpriteDimensions(image.getWidth(), image.getHeight());
		SpriteContents contents = new SpriteContents(id.getSpriteId(), dimensions, image, ResourceMetadata.NONE);
		id.setContents(contents);
		ATLAS_SPRITES.add(id);
		if (onAtlasStitched != null) {
			MyTotemDollAtlasManager.stitchAndUpdate(ATLAS_SPRITES, onAtlasStitched);
		}
	}

	public static void registerSkinSprite(Identifier id, NativeImage image, @Nullable OnSpriteUploaded onSpriteUploaded) {
		registerSpecialSprite(image, id, CACHED_SPECIAL_SKIN_SPRITES, true, onSpriteUploaded);
	}

	@Nullable
	public static AtlasSprite registerRemappedSprite(AtlasSprite sprite) {
		long cachedId = sprite.getCachedId();
		if (cachedId != -1 && CACHED_SPECIAL_REMAPPED_SPRITES.containsKey(cachedId)) {
			return sprite;
		}

		Identifier resourceId = sprite.getSpriteId();
		sprite.setSpriteId(MyTotemDoll.id("remapped_sprites/%s.png".formatted(MathHelper.abs(resourceId.toString().hashCode()))));

		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(resourceId).orElse(null);
		if (resource == null) {
			return null;
		}

		try {
			NativeImage image = NativeImage.read(resource.getInputStream());
			NativeImage remapped = PlayerSkinUtils.remapTextureToStandardSize(image, true);

			registerSpecialSprite(remapped, resourceId, CACHED_SPECIAL_REMAPPED_SPRITES, false, sprite::copyFrom);
			return sprite;
		} catch (IOException e) {
			MyTotemDollClient.LOGGER.error("Failed to remap texture as a sprite in atlas!", e);
		}

		return null;
	}

	private static void registerSpecialSprite(NativeImage image, Identifier id, LongObjectMap<AtlasSprite> specialSprites, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		long spriteUniqueId = AtlasSprite.generateUniqueIdByContent(image);
		AtlasSprite alreadyRegisteredSprite = specialSprites.get(spriteUniqueId);
		if (alreadyRegisteredSprite != null) {
			if (onSpriteUploaded != null) {
				onSpriteUploaded.onUploaded(alreadyRegisteredSprite);
			}
			return;
		}

		AtlasSprite sprite = AtlasSprite.of(id, image);

		sprite.setCachedId(spriteUniqueId);
		sprite.setClosable(false);
		sprite.setUnregisterAction(() -> {
			specialSprites.remove(spriteUniqueId);
			ATLAS_SPRITES.remove(sprite);
		});
		specialSprites.put(spriteUniqueId, sprite);
		ATLAS_SPRITES.add(sprite);

		if (stitchAndUpdate && onSpriteUploaded != null) {
			MyTotemDollAtlasManager.stitchAndUpdate(ATLAS_SPRITES, () -> onSpriteUploaded.onUploaded(sprite));
		}
	}

	public static void clear() {
		ATLAS_SPRITES.forEach(AtlasSprite::close);
		ATLAS_SPRITES.clear();
	}

	public static void reload() {
		MyTotemDollAtlasSpriteManager.clear();
		ATLAS_SPRITES.add(AtlasSprite.of(MissingSprite.createSpriteContents()));
		ATLAS_SPRITES.addAll(CACHED_SPECIAL_SKIN_SPRITES.values());
		ATLAS_SPRITES.addAll(CACHED_SPECIAL_REMAPPED_SPRITES.values());
		registerSprite(TotemDollSprites.STEVE_SKIN_SPRITE, null);
		registerRemappedSprite(TotemDollSprites.ELYTRA_SPRITE);
	}
}
