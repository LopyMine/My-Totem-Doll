package net.lopymine.mtd.doll.renderer.special;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemGuiElementRenderer extends PictureInPictureRenderer<ItemGuiRenderState> {

	private final ItemStackRenderState itemRenderState = new ItemStackRenderState();

	public ItemGuiElementRenderer(BufferSource vertexConsumers) {
		super(vertexConsumers);
	}

	@Override
	public Class<ItemGuiRenderState> getRenderStateClass() {
		return ItemGuiRenderState.class;
	}

	@Override
	protected void renderToTexture(ItemGuiRenderState state, PoseStack matrices) {
		Minecraft client = Minecraft.getInstance();

		client.gameRenderer.getLighting().setupFor(Entry.ITEMS_FLAT);
		matrices.mulPose(state.rotation());
		float size = state.size();
		matrices.scale(-size, -size, size);
		this.renderItem(
				state.stack(),
				ItemDisplayContext.FIXED,
				15728880,
				OverlayTexture.NO_OVERLAY,
				matrices,
				this.bufferSource,
				client.level,
				0
		);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
	}

	@Override
	protected String getTextureLabel() {
		return "%s-item-special-gui-renderer".formatted(MyTotemDoll.MOD_ID);
	}

	public void renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, PoseStack matrices, MultiBufferSource vertexConsumers, @Nullable Level world, int seed) {
		this.renderItem(null, stack, displayContext, matrices, vertexConsumers, world, light, overlay, seed);
	}

	public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, PoseStack matrices, MultiBufferSource vertexConsumers, @Nullable Level world, int light, int overlay, int seed) {
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.itemRenderState, stack, displayContext, world, entity, seed);
		FeatureRenderDispatcher dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
		this.itemRenderState.submit(matrices, dispatcher.getSubmitNodeStorage(), light, overlay, 0);
		dispatcher.renderAllFeatures();
	}
}
