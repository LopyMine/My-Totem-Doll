package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;

import org.jetbrains.annotations.Nullable;

@Mixin(Screen.class)
public abstract class ScreenMixin {

	@Shadow @Nullable
	public Minecraft minecraft;

	@Inject(at = @At("HEAD"), method = "renderDirtBackground", cancellable = true)
	private void disableBackgroundTextureRendering(GuiGraphics context, CallbackInfo ci) {
		if (!YACLConfigurationScreen.notOpen(((Screen) (Object) this)) && this.minecraft != null && this.minecraft.level != null) {
			ci.cancel();
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fillGradient(IIIIII)V"), method = "renderBackground")
	private void swapBackgroundGradientColor(GuiGraphics context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, Operation<Void> original) {
		if (!YACLConfigurationScreen.notOpen(((Screen) (Object) this)) && this.minecraft != null && this.minecraft.level != null) {
			original.call(context, startX, startY, endX, endY, 335544320, 335544320);
			return;
		}
		original.call(context, startX, startY, endX, endY, colorStart, colorEnd);
	}


	@Shadow public abstract void renderBackground(GuiGraphics context);

	@Inject(at = @At("HEAD"), method = "render")
	private void renderWithBackground(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!YACLConfigurationScreen.notOpen(((Screen)(Object)this))) {
			this.renderBackground(context);
		}
	}


}
