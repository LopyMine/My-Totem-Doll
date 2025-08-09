package net.lopymine.mtd.atlas;

import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.atlas.stitch.OnSpriteUploaded;
import net.minecraft.client.texture.*;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class AtlasSprite {

	private boolean closable = true;

	@NotNull
	private Identifier spriteId;
	@Nullable
	private SpriteContents contents;
	@Nullable
	private Runnable unregisterAction;
	private OnSpriteUploaded uploadAction;
	private volatile boolean uploaded;
	private long cachedId = -1;

	private AtlasSprite(@NotNull Identifier spriteId) {
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
		AtlasSprite atlasSprite = new AtlasSprite(contents.getId());
		atlasSprite.setContents(contents);
		return atlasSprite;
	}

	public static AtlasSprite of(Identifier spriteId, NativeImage image) {
		SpriteDimensions dimensions = new SpriteDimensions(image.getWidth(), image.getHeight());
		SpriteContents contents = new SpriteContents(spriteId, dimensions, image, ResourceMetadata.NONE);
		AtlasSprite atlasSprite = new AtlasSprite(spriteId);
		atlasSprite.setContents(contents);
		return atlasSprite;
	}

	public static long generateUniqueIdByContent(NativeImage image) {
		long uniqueId = 1125899906842597L;

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				uniqueId = 31 * uniqueId + image.getColorArgb(x, y);
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

	public void close() {
		this.uploaded = false;
		if (this.contents != null && this.closable) {
			this.contents.close();
			this.contents = null;
		}
	}

	public void closeAndUnregisterAnyway() {
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
		}
	}

	public void copyFrom(AtlasSprite registeredSprite) {
		this.closable = registeredSprite.isClosable();
		this.spriteId = registeredSprite.getSpriteId();
		this.contents = registeredSprite.getContents();
		this.unregisterAction = registeredSprite.getUnregisterAction();
		this.uploaded = registeredSprite.isUploaded();
		this.cachedId = registeredSprite.getCachedId();
	}
}
