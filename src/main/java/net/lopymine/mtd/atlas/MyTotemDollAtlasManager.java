package net.lopymine.mtd.atlas;

import java.io.IOException;
import java.util.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

public class MyTotemDollAtlasManager {

	public static final List<Identifier> MODEL_TEXTURES = new LinkedList<>();
	public static final List<SpriteContents> SPRITES = new LinkedList<>();
	@Nullable
	private static SpriteAtlasTexture ATLAS_TEXTURE;

	@NotNull
	public static SpriteAtlasTexture getNewInstance() {
		return new SpriteAtlasTexture(MyTotemDoll.id("main_atlas.png"));
	}

	@NotNull
	public static SpriteAtlasTexture getAtlasTexture() {
		if (ATLAS_TEXTURE == null) {
			return setAtlas(getNewInstance());
		}
		return ATLAS_TEXTURE;
	}

	public static SpriteAtlasTexture setAtlas(@NotNull SpriteAtlasTexture texture) {
		ATLAS_TEXTURE = texture;
		MinecraftClient.getInstance().getTextureManager().registerTexture(texture.getId(), texture);
		return ATLAS_TEXTURE;
	}

	public static Sprite getSprite(Identifier id) {
		return getAtlasTexture().getSprite(id);
	}

	public static void clear() {
		SPRITES.forEach(SpriteContents::close);
		SPRITES.clear();
	}

	public static void registerSprite(Identifier id) {
		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id).orElse(null);
		if (resource == null) {
			return;
		}
		try {
			registerSprite(id, NativeImage.read(resource.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void registerSprite(Identifier id, NativeImage image) {
		SpriteDimensions dimensions = new SpriteDimensions(image.getWidth(), image.getHeight());
		SpriteContents contents = new SpriteContents(id, dimensions, image, ResourceMetadata.NONE);
		SPRITES.add(contents);
		TotemDollAtlasReloadListener.reloadAtlas(SPRITES, MyTotemDollTaskExecutor.MAIN_EXECUTOR);
	}

}
