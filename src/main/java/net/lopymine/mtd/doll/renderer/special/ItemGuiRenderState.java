package net.lopymine.mtd.doll.renderer.special;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public record ItemGuiRenderState(
		@Nullable
		ItemStack stack,
		int x,
		int y,
		int width,
		int height,
		float size,
		Quaternionf rotation,
		@Nullable ScreenRectangle scissorArea,
		@Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

	public ItemGuiRenderState(
			@Nullable
			ItemStack stack,
			int x,
			int y,
			int width,
			int height,
			float size,
			Quaternionf rotation,
			@Nullable ScreenRectangle scissorArea
	) {
		this(stack, x, y, width, height, size, rotation, scissorArea, PictureInPictureRenderState.getBounds(x, y, x + width, y + height, scissorArea));
	}

	@Override
	public int x0() {
		return this.x();
	}

	@Override
	public int x1() {
		return this.x0() + this.width();
	}

	@Override
	public int y0() {
		return this.y();
	}

	@Override
	public int y1() {
		return this.y0() + this.height();
	}

	@Override
	public float scale() {
		return 1.0F;
	}
}
