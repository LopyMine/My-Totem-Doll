package net.lopymine.mtd.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.*;
import lombok.*;
import net.lopymine.mtd.atlas.stitch.OnSpriteUploaded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.*;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class AtlasSprite {

	public static final ResourceMetadata STANDARD_METADATA = ResourceMetadata.EMPTY;

	@NotNull
	private Identifier spriteId;
	@Nullable
	private SpriteContents contents;

	private long cachedId = -1;
	private boolean closable = true;
	@Nullable
	private Runnable unregisterAction;
	private OnSpriteUploaded uploadAction;

	private volatile boolean uploaded;

	public AtlasSprite(@NotNull Identifier spriteId) {
		this.spriteId = spriteId;
	}

	@Nullable
	public static AtlasSprite of(@Nullable Identifier spriteId) {
		if (spriteId == null) {
			return null;
		}
		return new AtlasSprite(spriteId);
	}

	public static AtlasSprite of(@Nullable SpriteContents contents) {
		if (contents == null) {
			return null;
		}
		AtlasSprite atlasSprite = new AtlasSprite(contents.name());
		atlasSprite.setContents(contents);
		return atlasSprite;
	}

	public static AtlasSprite of(Identifier spriteId, NativeImage image) {
		AtlasSprite atlasSprite = new AtlasSprite(spriteId);
		updateContents(atlasSprite, image);
		return atlasSprite;
	}

	public static void updateContents(AtlasSprite sprite, NativeImage image) {
		ResourceMetadata metadata = getAnimationMetadataForSprite(sprite);
		boolean animated = metadata != STANDARD_METADATA;
		int width = image.getWidth();
		int height = image.getHeight();
		int min = Math.min(width, height);

		FrameSize dimensions = animated ? new FrameSize(min, min) : new FrameSize(width, height);
		Optional<AnimationMetadataSection> decode = metadata.getSection(AnimationMetadataSection.TYPE);
		Optional<TextureMetadataSection> decode2 = metadata.getSection(TextureMetadataSection.TYPE);
		SpriteContents contents = new SpriteContents(sprite.getSpriteId(), dimensions, image, decode, List.of(), decode2);
		sprite.setContents(contents);
	}

	public static ResourceMetadata getAnimationMetadataForSprite(AtlasSprite sprite) {
		try {
			Identifier id = sprite.getSpriteId();
			InputStream stream = Minecraft.getInstance()
					.getResourceManager()
					.getResourceOrThrow(Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + ".mcmeta"))
					.open();

			return ResourceMetadata.fromJsonStream(stream);
		} catch (Exception ignored) {
			return STANDARD_METADATA;
		}
	}

	public static long generateUniqueIdByContent(NativeImage image) {
		long uniqueId = 1125899906842597L;

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				uniqueId = 31 * uniqueId + image.getPixel(x, y);
			}
		}

		return uniqueId;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof AtlasSprite that)) return false;
		return Objects.equals(this.getSpriteId(), that.getSpriteId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getSpriteId());
	}

	private boolean cannotClose(OnSpriteUploaded closeOnRegistered) {
		if (!this.uploaded) {
			this.uploadAction = this.uploadAction != null ? this.uploadAction.then(closeOnRegistered) : closeOnRegistered;
			return true;
		}
		return false;
	}

	public void close() {
		if (this.cannotClose(AtlasSprite::close)) {
			return;
		}
		this.uploaded = false;
		if (this.contents != null && this.closable) {
			this.contents.close();
			this.contents = null;
		}
	}

	public void closeAnyway() {
		if (this.cannotClose(AtlasSprite::closeAnyway)) {
			return;
		}
		this.uploaded = false;
		if (this.contents != null) {
			this.contents.close();
			this.contents = null;
		}
	}

	public void closeAndUnregister() {
		if (this.cannotClose(AtlasSprite::closeAndUnregister)) {
			return;
		}
		this.uploaded = false;
		if (this.contents != null && this.closable) {
			this.contents.close();
		}
		if (this.unregisterAction != null) {
			this.unregisterAction.run();
		}
	}

	public void closeAndUnregisterAnyway() {
		if (this.cannotClose(AtlasSprite::closeAndUnregisterAnyway)) {
			return;
		}
		this.uploaded = false;
		if (this.contents != null) {
			this.contents.close();
		}
		if (this.unregisterAction != null) {
			this.unregisterAction.run();
		}
	}

	public void markUploaded() {
		this.uploaded = true;
		if (this.uploadAction != null) {
			this.uploadAction.onUploaded(this);
			this.uploadAction = null;
		}
	}

	public void copyFrom(AtlasSprite registeredSprite) {
		this.closable         = registeredSprite.isClosable();
		this.spriteId         = registeredSprite.getSpriteId();
		this.contents         = registeredSprite.getContents();
		this.unregisterAction = registeredSprite.getUnregisterAction();
		this.uploaded         = registeredSprite.isUploaded();
		this.cachedId         = registeredSprite.getCachedId();
	}
}
