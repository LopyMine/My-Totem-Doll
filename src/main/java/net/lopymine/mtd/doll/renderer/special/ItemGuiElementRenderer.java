package net.lopymine.mtd.doll.renderer.special;

//? if >=1.21.6 {
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.DiffuseLighting.Type;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.9 {

import net.minecraft.client.render.command.*;

//?}

public class ItemGuiElementRenderer extends SpecialGuiElementRenderer<ItemGuiRenderState> {

	private final ItemRenderState itemRenderState = new ItemRenderState();

	public ItemGuiElementRenderer(Immediate vertexConsumers) {
		super(vertexConsumers);
	}

	@Override
	public Class<ItemGuiRenderState> getElementClass() {
		return ItemGuiRenderState.class;
	}

	@Override
	protected void render(ItemGuiRenderState state, MatrixStack matrices) {
		MinecraftClient client = MinecraftClient.getInstance();

		client.gameRenderer.getDiffuseLighting().setShaderLights(Type.ITEMS_FLAT);
		matrices.multiply(state.rotation());
		float size = state.size();
		matrices.scale(-size, -size, size);
		this.renderItem(
				state.stack(),
				ItemDisplayContext.FIXED,
				15728880,
				OverlayTexture.DEFAULT_UV,
				matrices,
				this.vertexConsumers,
				client.world,
				0
		);
	}

	@Override
	protected float getYOffset(int height, int windowScaleFactor) {
		return height / 2F;
	}

	@Override
	protected String getName() {
		return "%s-item-special-gui-renderer".formatted(MyTotemDoll.MOD_ID);
	}

	public void renderItem(ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int seed) {
		this.renderItem(null, stack, displayContext, matrices, vertexConsumers, world, light, overlay, seed);
	}

	public void renderItem(@Nullable LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
		MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(this.itemRenderState, stack, displayContext, world, entity, seed);
		//? if >=1.21.9 {
		RenderDispatcher dispatcher = MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher();
		this.itemRenderState.render(matrices, dispatcher.getQueue(), light, overlay, 0);
		dispatcher.render();
		//?} else {
		/*this.itemRenderState.render(matrices, vertexConsumers, light, overlay);
		*///?}
	}
}
//?}
