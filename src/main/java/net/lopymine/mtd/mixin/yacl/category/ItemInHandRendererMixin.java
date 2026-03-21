package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.Consumer;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.category.rendering.RenderingCategoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.ItemInHandRenderer.HandRenderSelection;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

	@Unique
	private static void createBoolean(LocalBooleanRef ref) {
		Minecraft client = Minecraft.getInstance();
		Screen currentScreen = client.screen;

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
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		ref.set(true);
	}

	@Unique
	private static void renderDoll(ItemStack original, Consumer<ItemStack> draw) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			draw.accept(original);
			return;
		}
		if (original.isEmpty() || !MyTotemDollClient.canProcess(original)) {
			ItemStack totem = Items.TOTEM_OF_UNDYING.getDefaultInstance();

			totem.set(DataComponents.CUSTOM_NAME, player.getName());

			draw.accept(totem);
			return;
		}
		draw.accept(original);
	}

	@Inject(
			at = @At("HEAD"),
			method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"
	)
	private void createBoolean(CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		createBoolean(ref);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderMainHand:Z"
			),
			method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"
	)
	private boolean swapRenderValue1(HandRenderSelection instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderOffHand:Z"
			),
			method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V"
	)
	private boolean swapRenderValue2(HandRenderSelection instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"), method = "renderHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/player/LocalPlayer;I)V")
	private void swapRenderingStack(ItemInHandRenderer instance, AbstractClientPlayer playerEntity, float a, float b, InteractionHand hand, float c, ItemStack stack, float d, PoseStack matrixStack, SubmitNodeCollector queue, int i, Operation<Void> original, @Share("mtd_bl") LocalBooleanRef ref) {
		Consumer<ItemStack> consumer = (itemStack) -> original.call(instance, playerEntity, a, b, hand, c, itemStack, d, matrixStack, queue, i);
		if (ref.get()) {
			renderDoll(stack, consumer);
		} else {
			consumer.accept(stack);
		}
	}
}
