package net.lopymine.mtd.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class RemappedAtlasSprite extends AtlasSprite {

	private Identifier resourceId;

	protected RemappedAtlasSprite(@NotNull Identifier resourceId, @NotNull Identifier spriteId) {
		super(spriteId);
		this.resourceId = resourceId;
	}

	public static RemappedAtlasSprite ofResource(@NotNull Identifier resourceId) {
		Identifier spriteId = MyTotemDoll.id("remapped_sprites/%s.png".formatted(Mth.abs(resourceId.toString().hashCode())));
		return new RemappedAtlasSprite(resourceId, spriteId);
	}

	public static RemappedAtlasSprite ofResource(Identifier resourceId, NativeImage image) {
		RemappedAtlasSprite remappedAtlasSprite = ofResource(resourceId);
		updateContents(remappedAtlasSprite, image);
		return remappedAtlasSprite;
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

	@Override
	public void copyFrom(AtlasSprite registeredSpriteWithResourceIds) {
		if (registeredSpriteWithResourceIds instanceof RemappedAtlasSprite remappedAtlasSprite) {
			this.resourceId = remappedAtlasSprite.getResourceId();
		}
		super.copyFrom(registeredSpriteWithResourceIds);
	}
}
