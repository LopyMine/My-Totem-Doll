package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {

	@WrapWithCondition(method = "renderBackground", at = @At(value = "INVOKE",
			target =
					"Lnet/minecraft/client/gui/screens/Screen;renderBlurredBackground(F)V"
	))
	public boolean disableBlur(Screen instance, float v) {
		return YACLConfigurationScreen.notOpen(((Screen) (Object) this));
	}

	@ModifyArg(method = "renderMenuBackground(Lnet/minecraft/client/gui/GuiGraphics;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderMenuBackgroundTexture(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIFFII)V"), index = 1)
	private ResourceLocation swapBackgroundTexture(ResourceLocation original) {
		if (YACLConfigurationScreen.notOpen(((Screen) (Object) this))) {
			return original;
		}
		return TransparencySprites.getMenuBackgroundTexture();
	}
}
