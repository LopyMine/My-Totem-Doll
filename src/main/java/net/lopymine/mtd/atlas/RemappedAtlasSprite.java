package net.lopymine.mtd.atlas;

import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class RemappedAtlasSprite extends AtlasSprite {

	private Identifier resourceId;

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
		Identifier spriteId = MyTotemDoll.id("remapped_sprites/%s.png".formatted(MathHelper.abs(resourceId.toString().hashCode())));
		return new RemappedAtlasSprite(resourceId, spriteId);
	}

	public static RemappedAtlasSprite ofResource(Identifier resourceId, NativeImage image) {
		RemappedAtlasSprite remappedAtlasSprite = ofResource(resourceId);
		updateContents(remappedAtlasSprite, image);
		return remappedAtlasSprite;
	}

	@Override
	public void copyFrom(AtlasSprite registeredSpriteWithResourceIds) {
		if (registeredSpriteWithResourceIds instanceof RemappedAtlasSprite remappedAtlasSprite) {
			this.resourceId = remappedAtlasSprite.getResourceId();
		}
		super.copyFrom(registeredSpriteWithResourceIds);
	}
}
