package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.modmenu.yacl.*;

@Pseudo
@Mixin(TabNavigationWidget.class)
public class TabNavigationWidgetMixin {

	//? if <=1.20.4 {

	/*@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"), method = "render")
	private void renderTransparencyHeaderSeparator(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, texture, x, y, u, v, width, height, textureWidth, textureHeight);
			return;
		}
		RenderSystem.enableBlend();
		context.drawTexture(TransparencySprites.HEADER_SEPARATOR_TEXTURE, x, y, u, x, width, height, textureWidth, textureHeight);
		RenderSystem.disableBlend();
	}

	@Dynamic
	@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"), method = "render")
	private boolean disableBlackBackground(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
		return YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen);
	}

	*///?}

}
