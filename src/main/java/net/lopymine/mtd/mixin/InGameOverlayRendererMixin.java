package net.lopymine.mtd.mixin;

//? if >=1.21.6 {

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.doll.renderer.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

	//? if >=1.21.9 {

	@Shadow private @Nullable ItemStack floatingItem;

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;III)V"
			),
			method = "renderFloatingItem"
	)
	private void renderFloatingDoll(ItemRenderState instance, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, int uv, int i, Operation<Void> original) {
		if (!TotemDollRenderer.sentRenderRequest(matrices, this.floatingItem, DollRenderContext.D_FLOATING, light, uv, 0)) {
			original.call(instance, matrices, orderedRenderCommandQueue, light, uv, i);
		}
	}

	//?} else {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;I)V"), method = "renderFloatingItem")
	private void renderFloatingDoll(ItemRenderer instance, ItemStack stack, ItemDisplayContext displayContext, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int seed, Operation<Void> original) {
		if (!TotemDollRendereddr.renderedFloatingDoll(matrices, stack, vertexConsumers, light, overlay)) {
			original.call(instance, stack, displayContext, light, overlay, matrices, vertexConsumers, world, seed);
		}
	}
	*///?}

}
//?}
