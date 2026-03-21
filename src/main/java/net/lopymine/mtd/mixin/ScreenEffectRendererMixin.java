package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.mtd.doll.renderer.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	@Shadow
	private @Nullable ItemStack itemActivationItem;

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
			),
			method = "renderItemActivationAnimation"
	)
	private void renderFloatingDoll(ItemStackRenderState instance, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, int uv, int i, Operation<Void> original) {
		if (!TotemDollRenderer.sentRenderRequest(matrices, this.itemActivationItem, DollRenderContext.D_FLOATING, light, uv, 0, null)) {
			original.call(instance, matrices, orderedRenderCommandQueue, light, uv, i);
		}
	}

}
