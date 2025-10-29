package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;


import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.category.rendering.RenderingCategoryTab;

//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
//?}

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	//? if >=1.21.9 {
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"), method = "renderFirstPersonItem")
	private void renderDollPreview(HeldItemRenderer instance, LivingEntity livingEntity, ItemStack stack, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue queue, int i, Operation<Void> original) {
		Consumer<ItemStack> draw = (item) -> original.call(instance, livingEntity, stack, itemDisplayContext, matrixStack, queue, i);
		renderDoll(stack, draw);
	}
	//?} else {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")
	private void renderDollPreview(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
		renderDoll(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light, original);
	}
	*///?}

	@Unique
	private static void renderDoll(ItemStack original, Consumer<ItemStack> draw) {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen currentScreen = client.currentScreen;

		if (YACLConfigurationScreen.notOpen(currentScreen)) {
			draw.accept(original);
			return;
		}
		if (!(currentScreen instanceof YACLScreen yaclScreen)) {
			draw.accept(original);
			return;
		}
		if (!(yaclScreen.tabManager.getCurrentTab() instanceof RenderingCategoryTab)) {
			draw.accept(original);
			return;
		}

		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			draw.accept(original);
			return;
		}

		if (original.isEmpty() || !original.isOf(Items.TOTEM_OF_UNDYING)) {
			ItemStack totem = Items.TOTEM_OF_UNDYING.getDefaultStack();


			//? if >=1.20.5 {
			totem.set(DataComponentTypes.CUSTOM_NAME, player.getName());
			//?} else {
			/*totem.setCustomName(player.getName());
			 *///?}

			draw.accept(totem);
			return;
		}

		draw.accept(original);
	}
}
