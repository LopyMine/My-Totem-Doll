package net.lopymine.mtd.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class RemappedAtlasSprite extends AtlasSprite {

	private ResourceLocation resourceId;

	protected RemappedAtlasSprite(@NotNull ResourceLocation resourceId, @NotNull ResourceLocation spriteId) {
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

	public static RemappedAtlasSprite ofResource(@NotNull ResourceLocation resourceId) {
		ResourceLocation spriteId = MyTotemDoll.id("remapped_sprites/%s.png".formatted(Mth.abs(resourceId.toString().hashCode())));
		return new RemappedAtlasSprite(resourceId, spriteId);
	}

	public static RemappedAtlasSprite ofResource(ResourceLocation resourceId, NativeImage image) {
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
