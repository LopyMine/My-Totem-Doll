package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.modmenu.yacl.*;

import org.jetbrains.annotations.Nullable;

@Pseudo
@Mixin(Screen.class)
public abstract class ScreenMixin {

	//? if >=1.20.5 {
	@Dynamic
	@WrapWithCondition(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;applyBlur(F)V"))
	public boolean disableBlur(Screen instance, float v) {
		return YACLConfigurationScreen.notOpen(((Screen) (Object) this));
	}

	@Dynamic
	@ModifyArg(method = "renderDarkening(Lnet/minecraft/client/gui/DrawContext;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackgroundTexture(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;IIFFII)V"), index = 1)
	private Identifier swapBackgroundTexture(Identifier original) {
		if (YACLConfigurationScreen.notOpen(((Screen) (Object) this))) {
			return original;
		}
		return TransparencySprites.MENU_BACKGROUND_TEXTURE_V2;
	}
	//?} else {

	/*@Dynamic
	@Shadow @Nullable
	public MinecraftClient client;

	@Dynamic
	@Inject(at = @At("HEAD"), method = "renderBackgroundTexture", cancellable = true)
	private void disableBackgroundTextureRendering(DrawContext context, CallbackInfo ci) {
		if (!YACLConfigurationScreen.notOpen(((Screen) (Object) this)) && this.client != null && this.client.world != null) {
			ci.cancel();
		}
	}

	@Unique
	private static final String INJECT_METHOD = /^? >=1.20.2 {^/ /^"renderInGameBackground" ^//^?} else {^/ "renderBackground" /^?}^/;

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fillGradient(IIIIII)V"), method = INJECT_METHOD)
	private void swapBackgroundGradientColor(DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, Operation<Void> original) {
		if (!YACLConfigurationScreen.notOpen(((Screen) (Object) this)) && this.client != null && this.client.world != null) {
			original.call(context, startX, startY, endX, endY, 335544320, 335544320);
			return;
		}
		original.call(context, startX, startY, endX, endY, colorStart, colorEnd);
	}

	//? if <=1.20.1 {

	@Dynamic
	@Shadow public abstract void renderBackground(DrawContext context);

	@Dynamic
	@Inject(at = @At("HEAD"), method = "render")
	private void renderWithBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!YACLConfigurationScreen.notOpen(((Screen)(Object)this))) {
			this.renderBackground(context);
		}
	}

	//?}

	*///?}
}
