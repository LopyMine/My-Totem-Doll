package net.lopymine.mtd.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.LightningUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Nullable
	private ItemStack itemActivationItem;

	@SuppressWarnings("deprecation")
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"), method = "renderItemActivationAnimation")
	private void renderFloatingDoll(GuiGraphics drawContext, Runnable drawCallback, Operation<Void> original, @Local PoseStack matrices) {
		drawContext.drawManaged(() -> {
			ItemStack stack = this.itemActivationItem;
			if (stack == null || !TotemDollRenderer.canRender(stack)) {
				original.call(drawContext, drawCallback);
				return;
			}
			LightningUtils.disable3dLighting();
			LocalPlayer player = Minecraft.getInstance().player;
			boolean bl = MyTotemDollConfig.getInstance().getStandardTotemDollSkinType() == TotemDollSkinType.HOLDING_PLAYER && player != null;
			if (bl) {
				stack.setPlayerEntity(player);
			}
			TotemDollRenderer.renderAnyway(matrices, stack, DollRenderContext.D_FLOATING, 15728880, OverlayTexture.NO_OVERLAY, 0, drawContext.bufferSource);
			if (bl) {
				stack.setPlayerEntity(null);
			}
			LightningUtils.disable3dLighting();
		});
	}

}

