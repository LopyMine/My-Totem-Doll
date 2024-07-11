package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
 //?}
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.modmenu.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.modmenu.yacl.custom.category.tab.RenderingCategoryTab;

@Pseudo
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"), method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")
	private void renderDoll(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen currentScreen = client.currentScreen;

		if (YACLConfigurationScreen.notOpen(currentScreen)) {
			original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
			return;
		}
		if (!(currentScreen instanceof YACLScreen yaclScreen)) {
			original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
			return;
		}
		if (!(yaclScreen.tabManager.getCurrentTab() instanceof RenderingCategoryTab)) {
			original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
			return;
		}

		if (item.isEmpty() || !item.isOf(Items.TOTEM_OF_UNDYING)) {
			ItemStack totem = Items.TOTEM_OF_UNDYING.getDefaultStack();

			//? if >=1.20.5 {
			totem.set(DataComponentTypes.CUSTOM_NAME, player.getName());
			 //?} else {
			/*totem.setCustomName(player.getName());
			*///?}

			original.call(instance, player, tickDelta, pitch, hand, swingProgress, totem, equipProgress, matrices, vertexConsumers, light);
		} else {
			original.call(instance, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
		}
	}
}
