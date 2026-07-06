package net.lopymine.mtd.mixin;


import net.lopymine.mtd.doll.renderer.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.ItemDisplayContext;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"), method = "renderItemActivationAnimation")
	private void renderFloatingDoll(ItemRenderer itemRenderer, ItemStack stack, ItemDisplayContext transformationType, int light, int overlay, PoseStack matrices, MultiBufferSource vertexConsumers, Level world, int seed, Operation<Void> original) {
		if (!TotemDollRenderer.sentRenderRequest(matrices, stack, DollRenderContext.D_FLOATING, light, overlay, 0, vertexConsumers)) {
			original.call(itemRenderer, stack, transformationType, light, overlay, matrices, vertexConsumers, world, seed);
		}
	}

}

