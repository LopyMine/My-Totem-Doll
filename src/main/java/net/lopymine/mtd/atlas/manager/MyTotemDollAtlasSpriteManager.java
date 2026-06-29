package net.lopymine.mtd.atlas.manager;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.stitch.OnSpriteUploaded;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasSpriteManager {

	@NotNull
	public static final AtlasSprite STEVE_SKIN_SPRITE = Objects.requireNonNull(AtlasSprite.of(Identifier.fromNamespaceAndPath("minecraft", "textures/entity/player/wide/steve.png")));
	@NotNull
	public static final RemappedAtlasSprite ELYTRA_SPRITE = RemappedAtlasSprite.ofResource(Identifier.parse("textures/entity/equipment/wings/elytra.png"));
	private static final AtlasSprite MISSING_SPRITE = AtlasSprite.of(MissingTextureAtlasSprite.create());
	private static final Map<Long, AtlasSprite> CONTENT_CACHED_SPECIAL_SKIN_SPRITES = new ConcurrentHashMap<>();
	private static final Map<Long, AtlasSprite> CONTENT_CACHED_SPECIAL_REMAPPED_SPRITES = new ConcurrentHashMap<>();
	private static final Map<Identifier, AtlasSprite> DYNAMIC_SPRITES = new ConcurrentHashMap<>();

	private static final AtomicReference<Set<AtlasSprite>> ATLAS_SPRITES = new AtomicReference<>(Set.of());

	static {
		MISSING_SPRITE.setClosable(false);
		MISSING_SPRITE.setUnregisterAction(() -> handleSprite(MISSING_SPRITE, false));

		STEVE_SKIN_SPRITE.setClosable(false);
		STEVE_SKIN_SPRITE.setUnregisterAction(() -> handleSprite(STEVE_SKIN_SPRITE, false));
	}

	public static Set<AtlasSprite> getSprites() {
		return ATLAS_SPRITES.get();
	}

	public static void registerDynamicSprite(Identifier id, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		loadFromResource(id, (image) -> registerDynamicSprite(id, image, stitchAndUpdate, onSpriteUploaded));
	}

	public static void registerDynamicSprite(Identifier id, NativeImage image, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		SpriteFactory factory = () -> {
			AtlasSprite sprite = AtlasSprite.of(id, image);
			sprite.setUnregisterAction(() -> handleSprite(sprite, false));
			return sprite;
		};

		AtlasSprite createdSprite = createAndRegisterSprite(id, factory, DYNAMIC_SPRITES, onSpriteUploaded);
		if (createdSprite == null) {
			return;
		}

		uploadSprite(stitchAndUpdate, onSpriteUploaded, createdSprite);
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
			long cachedId = sprite.getCachedId();
			if (cachedId != -1 && CONTENT_CACHED_SPECIAL_REMAPPED_SPRITES.containsKey(cachedId)) {
				return;
			}
		}

		Identifier resourceId = sprite.getResourceId();
		loadFromResource(resourceId, (image) -> {
			NativeImage remapped = PlayerSkinUtils.remapTextureToStandardSize(image, true);
			registerSpecialContentCachedSprite(remapped, resourceId, CONTENT_CACHED_SPECIAL_REMAPPED_SPRITES, RemappedAtlasSprite::ofResource, false, sprite::copyFrom);
		});
	}

	private static void registerSpecialContentCachedSprite(NativeImage image, Identifier id, Map<Long, AtlasSprite> specialSprites, BiFunction<Identifier, NativeImage, AtlasSprite> spriteFactory, boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded) {
		long spriteUniqueId = AtlasSprite.generateUniqueIdByContent(image);

		SpriteFactory factory = () -> {
			AtlasSprite sprite = spriteFactory.apply(id, image);

			sprite.setCachedId(spriteUniqueId);
			sprite.setClosable(false);
			sprite.setUnregisterAction(() -> {
				specialSprites.remove(spriteUniqueId);
				handleSprite(sprite, false);
			});

			return sprite;
		};

		AtlasSprite createdSprite = createAndRegisterSprite(spriteUniqueId, factory, specialSprites, onSpriteUploaded);
		if (createdSprite == null) {
			return;
		}

		uploadSprite(stitchAndUpdate, onSpriteUploaded, createdSprite);
	}

	private static void uploadSprite(boolean stitchAndUpdate, @Nullable OnSpriteUploaded onSpriteUploaded, AtlasSprite createdSprite) {
		if (stitchAndUpdate && onSpriteUploaded != null) {
			MyTotemDollAtlasManager.stitchAndUpdate(getSprites(), () -> onSpriteUploaded.onUploaded(createdSprite));
		} else {
			createdSprite.setUploadAction(onSpriteUploaded);
		}
	}

	@Nullable
	private static <K> AtlasSprite createAndRegisterSprite(K key, SpriteFactory factory, Map<K, AtlasSprite> map, @Nullable OnSpriteUploaded onSpriteUploaded) {
		AtlasSprite alreadyRegisteredSprite = map.get(key);
		if (alreadyRegisteredSprite != null) {
			if (onSpriteUploaded != null) {
				onSpriteUploaded.onUploaded(alreadyRegisteredSprite);
			}
			return null;
		}

		AtlasSprite sprite = factory.create();
		map.put(key, sprite);
		handleSprite(sprite, true);
		return sprite;
	}

	private static void loadFromResource(Identifier id, Consumer<NativeImage> consumer) {
		Resource resource = Minecraft.getInstance().getResourceManager().getResource(id).orElse(null);
		if (resource == null) {
			AbstractTexture texture = Minecraft.getInstance().getTextureManager().byPath.get(id);

			if (!(texture instanceof DynamicTexture backedTexture)) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Failed to find texture even from TextureManager! Id: \"{}\", Texture Class: \"{}\"", id, texture == null ? "null" : texture.getClass().getSimpleName());
				return;
			}
			NativeImage image = backedTexture.getPixels();
			if (image == null) {
				MyTotemDollClient.LOGGER.error("Failed to register mod's texture as a sprite in atlas! Found image in TextureManager, but it's null somehow!? Id: \"{}\"", id);
				return;
			}

			NativeImage nativeImage = new NativeImage(image.getWidth(), image.getHeight(), true);
			nativeImage.copyFrom(image);
			consumer.accept(nativeImage);
			return;
		}
		try {
			consumer.accept(NativeImage.read(resource.open()));
		} catch (IOException e) {
			MyTotemDollClient.LOGGER.error("Failed to load resource for mod's atlas:", e);
		}
	}

	private static void handleSprite(AtlasSprite sprite, boolean add) {
		while (true) {
			AtomicReference<Set<AtlasSprite>> reference = ATLAS_SPRITES;
			Set<AtlasSprite> oldSprites = reference.get();
			Set<AtlasSprite> updatedSprites = new HashSet<>(oldSprites);
			if (add) {
				updatedSprites.add(sprite);
			} else {
				updatedSprites.remove(sprite);
			}
			if (reference.compareAndSet(oldSprites, Set.copyOf(updatedSprites))) {
				break;
			}
		}
	}

	public static void close() {
		Set<AtlasSprite> sprites = getSprites();
		sprites.forEach(AtlasSprite::closeAnyway);
	}

	public static void reload() {
		DYNAMIC_SPRITES.entrySet().removeIf((entry) -> {
			entry.getValue().closeAndUnregister();
			return true;
		});

		createAndRegisterSprite(MISSING_SPRITE.getSpriteId(), () -> MISSING_SPRITE, DYNAMIC_SPRITES, null);
		registerDynamicSprite(STEVE_SKIN_SPRITE.getSpriteId(), false, null);

		registerSpecialRemappedSprite(ELYTRA_SPRITE, false);
	}

	private interface SpriteFactory {

		AtlasSprite create();

	}
}
