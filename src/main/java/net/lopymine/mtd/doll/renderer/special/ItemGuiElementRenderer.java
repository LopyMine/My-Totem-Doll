package net.lopymine.mtd.doll.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.LightningUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.*;

public class ItemGuiElementRenderer extends PictureInPictureRenderer<ItemGuiRenderState> {

	private final ItemStackRenderState itemRenderState = new ItemStackRenderState();

	@Override
	protected void renderToTexture(ItemGuiRenderState state, PoseStack matrices, SubmitNodeCollector collector) {
		LightningUtils.flat();
		matrices.mulPose(state.rotation());
		float size = state.size();
		matrices.scale(-size, -size, size);
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(
				this.itemRenderState,
				state.stack(),
				ItemDisplayContext.FIXED,
				Minecraft.getInstance().level,
				null,
				0
		);
		this.itemRenderState.submit(matrices, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

	@Override
	public Class<ItemGuiRenderState> getRenderStateClass() {
		return ItemGuiRenderState.class;
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
	}

	@Override
	protected String getTextureLabel() {
		return "%s-item-special-gui-renderer".formatted(MyTotemDoll.MOD_ID);
	}
}
