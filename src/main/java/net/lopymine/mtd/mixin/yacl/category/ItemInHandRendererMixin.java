package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.isxander.yacl3.gui.YACLScreen;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.category.rendering.RenderingCategoryTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.3 {
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState.HandRenderSelection;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.ItemInHandRenderer.HandRenderSelection;
import net.minecraft.world.InteractionHand;
*///?}

//? if >=26.3 {
@Mixin(FirstPersonHandsAndItems.class)
//?} else {
/*@Mixin(ItemInHandRenderer.class)
*///?}
public class ItemInHandRendererMixin {

	//? if >=26.3 {
	@Inject(
			at = @At("HEAD"),
			method = "extractRenderState"
	)
	private void createBoolean(CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		myTotemDoll$createBoolean(ref);
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/FirstPersonHandsAndItems;evaluateWhichHandsToRender(Lnet/minecraft/client/player/LocalPlayer;)Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState$HandRenderSelection;"
			),
			method = "extractRenderState"
	)
	private HandRenderSelection swapRenderSelection(LocalPlayer player, Operation<HandRenderSelection> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return HandRenderSelection.RENDER_BOTH_HANDS;
		}
		return original.call(player);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/player/FirstPersonHandsAndItems;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
					opcode = Opcodes.GETFIELD
			),
			method = "extractRenderState"
	)
	private ItemStack swapMainHandStack(FirstPersonHandsAndItems instance, Operation<ItemStack> original, @Share("mtd_bl") LocalBooleanRef ref) {
		ItemStack stack = original.call(instance);
		return ref.get() ? myTotemDoll$getDollStack(stack) : stack;
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/player/FirstPersonHandsAndItems;offHandItem:Lnet/minecraft/world/item/ItemStack;",
					opcode = Opcodes.GETFIELD
			),
			method = "extractRenderState"
	)
	private ItemStack swapOffHandStack(FirstPersonHandsAndItems instance, Operation<ItemStack> original, @Share("mtd_bl") LocalBooleanRef ref) {
		ItemStack stack = original.call(instance);
		return ref.get() ? myTotemDoll$getDollStack(stack) : stack;
	}
	//?} else {
	/*@Inject(
			at = @At("HEAD"),
			method = "submitHandsWithItems"
	)
	private void createBoolean(CallbackInfo ci, @Share("mtd_bl") LocalBooleanRef ref) {
		myTotemDoll$createBoolean(ref);
	}

	@WrapOperation(
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderMainHand:Z",
					opcode = Opcodes.GETFIELD
			),
			method = "submitHandsWithItems"
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
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer$HandRenderSelection;renderOffHand:Z",
					opcode = Opcodes.GETFIELD),
			method = "submitHandsWithItems"
	)
	private boolean swapRenderValue2(HandRenderSelection instance, Operation<Boolean> original, @Share("mtd_bl") LocalBooleanRef ref) {
		if (ref.get()) {
			return true;
		}
		return original.call(instance);
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;submitArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
			),
			method = "submitHandsWithItems"
	)
	private void swapRenderingStack(ItemInHandRenderer instance, AbstractClientPlayer playerEntity, float a, float b, InteractionHand hand, float c, ItemStack stack, float d, PoseStack matrixStack, SubmitNodeCollector queue, int i, Operation<Void> original, @Share("mtd_bl") LocalBooleanRef ref) {
		original.call(instance, playerEntity, a, b, hand, c, ref.get() ? myTotemDoll$getDollStack(stack) : stack, d, matrixStack, queue, i);
	}
	*///?}

	@Unique
	private static void myTotemDoll$createBoolean(LocalBooleanRef ref) {
		Minecraft client = Minecraft.getInstance();
		Screen currentScreen = client.gui.screen();

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
	private static ItemStack myTotemDoll$getDollStack(ItemStack original) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return original;
		}
		if (original.isEmpty() || !MyTotemDollClient.canProcess(original)) {
			ItemStack totem = Items.TOTEM_OF_UNDYING.getDefaultInstance();

			totem.set(DataComponents.CUSTOM_NAME, player.getName());

			return totem;
		}
		return original;
	}
}
