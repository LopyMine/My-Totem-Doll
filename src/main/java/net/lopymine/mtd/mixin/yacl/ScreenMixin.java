package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {

	@WrapWithCondition(method = "extractBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"))
	public boolean disableBlur(Screen instance, GuiGraphicsExtractor context) {
		return YACLConfigurationScreen.notOpen(((Screen) (Object) this));
	}

	@ModifyArg(method = "extractMenuBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractMenuBackgroundTexture(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;IIFFII)V"), index = 1)
	private Identifier swapBackgroundTexture(Identifier original) {
		if (YACLConfigurationScreen.notOpen(((Screen) (Object) this))) {
			return original;
		}
		return TransparencySprites.getMenuBackgroundTexture();
	}

}
