package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;

import net.minecraft.client.render.item.HeldItemRenderer.HandRenderType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.*;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.category.rendering.RenderingCategoryTab;

import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
//?}

//? if >=1.21.9 {
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
 //?}

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	//? if >=1.21.9 {
	@Inject(
			at = @At("HEAD"),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private void createBoolean(CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		createBoolean(ref);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;renderMainHand:Z"
			),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private boolean swapRenderValue1(HandRenderType instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;renderOffHand:Z"
			),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private boolean swapRenderValue2(HandRenderType instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"), method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V")
	private void swapRenderingStack(HeldItemRenderer instance, AbstractClientPlayerEntity playerEntity, float a, float b, Hand hand, float c, ItemStack stack, float d, MatrixStack matrixStack, OrderedRenderCommandQueue queue, int i, Operation<Void> original, @Share("mtd_bl") LocalBooleanRef ref) {
		Consumer<ItemStack> consumer = (itemStack) -> original.call(instance, playerEntity, a, b, hand, c, itemStack, d, matrixStack, queue, i);
		if (ref.get()) {
			renderDoll(stack, consumer);
		} else {
			consumer.accept(stack);
		}
	}
	//?} else {
	/*@Inject(
			at = @At("HEAD"),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private void createBoolean(CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		createBoolean(ref);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;renderMainHand:Z"
			),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private boolean swapRenderValue1(HandRenderType instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer$HandRenderType;renderOffHand:Z"
			),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private boolean swapRenderValue2(HandRenderType instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
			),
			method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"
	)
	private void swapRenderingStack(HeldItemRenderer instance, AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original, @Share("mtd_bl") LocalBooleanRef ref) {
		Consumer<ItemStack> consumer = (itemStack) -> original.call(instance, player, tickProgress, pitch, hand, swingProgress, itemStack, equipProgress, matrices, vertexConsumers, light);
		if (ref.get()) {
			renderDoll(stack, consumer);
		} else {
			consumer.accept(stack);
		}
	}
	*///?}

	@Unique
	private static void createBoolean(LocalBooleanRef ref) {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen currentScreen = client.currentScreen;

		ref.set(false);
		if (YACLConfigurationScreen.notOpen(currentScreen)) {
			return;
		}
		if (!(currentScreen instanceof YACLScreen yaclScreen)) {
			return;
		}
		if (!(yaclScreen.tabManager.getCurrentTab() instanceof RenderingCategoryTab)) {
			return;
		}
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			return;
		}
		ref.set(true);
	}

	@Unique
	private static void renderDoll(ItemStack original, Consumer<ItemStack> draw) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) {
			draw.accept(original);
			return;
		}
		if (original.isEmpty() || !MyTotemDollClient.canProcess(original)) {
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
