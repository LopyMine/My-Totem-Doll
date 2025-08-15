package net.lopymine.mtd.atlas;

import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.atlas.stitch.OnSpriteUploaded;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.*;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class RemappedAtlasSprite extends AtlasSprite {

	private Identifier resourceId;

	protected RemappedAtlasSprite(@NotNull Identifier spriteId) {
		this(spriteId, spriteId);
	}

	protected RemappedAtlasSprite(@NotNull Identifier resourceId, @NotNull Identifier spriteId) {
		super(spriteId);
		this.resourceId = resourceId;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RemappedAtlasSprite that)) return false;
		return Objects.equals(this.getResourceId(), that.getResourceId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getResourceId());
	}

	public static RemappedAtlasSprite ofResource(@NotNull Identifier resourceId) {
		return new RemappedAtlasSprite(resourceId, resourceId);
	}

	public static RemappedAtlasSprite ofRemapped(Identifier spriteId, NativeImage image) {
		RemappedAtlasSprite atlasSprite = new RemappedAtlasSprite(spriteId);
		updateContents(atlasSprite, image);
		return atlasSprite;
	}

	@Override
	public void copyFrom(AtlasSprite registeredSprite) {
		if (registeredSprite instanceof RemappedAtlasSprite remappedAtlasSprite) {
			this.resourceId = remappedAtlasSprite.getResourceId();
		}
		super.copyFrom(registeredSprite);
	}
}
