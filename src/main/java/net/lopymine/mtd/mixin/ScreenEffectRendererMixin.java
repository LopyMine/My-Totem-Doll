package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.LightningUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@ExtensionMethod(ItemStackExtension.class)
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
	private void renderFloatingDoll(ItemStackRenderState instance, PoseStack matrices, SubmitNodeCollector collector, int lightCoords, int overlayCoords, int outlineColor, Operation<Void> original) {
		ItemStack stack = this.itemActivationItem;
		if (!TotemDollRenderer.canSubmit(stack) || stack == null) {
			original.call(instance, matrices, collector, lightCoords, overlayCoords, outlineColor);
			return;
		}
		LightningUtils.disable3dLighting();
		LocalPlayer player = Minecraft.getInstance().player;
		boolean bl = MyTotemDollConfig.getInstance().getStandardTotemDollSkinType() == TotemDollSkinType.HOLDING_PLAYER && player != null;
		if (bl) {
			stack.setPlayerEntity(player);
		}
		TotemDollRenderer.submitAnyway(collector, matrices, stack, DollRenderContext.D_FLOATING, lightCoords, overlayCoords, outlineColor);
		if (bl) {
			stack.setPlayerEntity(null);
		}
	}

}
