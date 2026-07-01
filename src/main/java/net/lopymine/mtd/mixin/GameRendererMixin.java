package net.lopymine.mtd.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.utils.LightningUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Shadow
	@Nullable
	private ItemStack itemActivationItem;

	@SuppressWarnings("deprecation")
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"), method = "renderItemActivationAnimation")
	private void renderFloatingDoll(GuiGraphics drawContext, Runnable drawCallback, Operation<Void> original, @Local PoseStack matrices) {
		drawContext.drawManaged(() -> {
			LightningUtils.disable3dLighting();
			if (!TotemDollRenderer.sentRenderRequest(matrices, this.itemActivationItem, DollRenderContext.D_FLOATING, 15728880, OverlayTexture.NO_OVERLAY, 0, drawContext.bufferSource)) {
				original.call(drawContext, drawCallback);
			}
		});
	}

}

