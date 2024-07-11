package net.lopymine.mtd.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.yacl3.gui.AbstractWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.modmenu.yacl.*;

@Pseudo
@Mixin(AbstractWidget.class)
public class AbstractWidgetMixin {

	//? if (=1.20.2 || =1.20.3) {

	/*@Dynamic
	@WrapOperation(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void renderTransparencyWidget(DrawContext drawContext, Identifier identifier, int x, int y, int width, int height, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) boolean hovered, @Local(argsOnly = true, ordinal = 1) boolean enabled) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(drawContext, identifier, x, y, width, height);
			return;
		}
		RenderSystem.enableBlend();
		drawContext.drawGuiTexture(TransparencySprites.WIDGET_SPRITES.get(enabled, hovered), x, y, width, height);
		RenderSystem.disableBlend();
	}

	*///?} elif >=1.20.1 {

	@Dynamic
	@WrapOperation(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/gui/utils/YACLRenderHelper;renderButtonTexture(Lnet/minecraft/client/gui/DrawContext;IIIIZZ)V"))
	private void renderTransparencyWidget(DrawContext drawContext, int x, int y, int width, int height, boolean enabled, boolean hovered, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(drawContext, x, y, width, height, enabled, hovered);
			return;
		}

		//? if >=1.20.2 {
		RenderSystem.enableBlend();
		drawContext.drawGuiTexture(TransparencySprites.WIDGET_SPRITES.get(enabled, hovered), x, y, width, height);
		RenderSystem.disableBlend();
		//?} else {
		/*RenderSystem.enableBlend();
		drawContext.drawNineSlicedTexture(TransparencySprites.WIDGET_SPRITES.get(enabled, hovered), x, y, width, height, 2, 256, 22, 0,0);
		RenderSystem.disableBlend();
		*///?}
	}
	//?} else {
	/*@Dynamic
	@WrapOperation(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V", ordinal = 0))
	private void renderTransparencyWidget2(DrawContext drawContext, Identifier texture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) boolean hovered, @Local(argsOnly = true, ordinal = 1) boolean enabled) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(drawContext, texture, x, y, z, u, v, width, height, textureWidth, textureHeight);
			return;
		}
		RenderSystem.enableBlend();
		drawContext.drawNineSlicedTexture(TransparencySprites.WIDGET_SPRITES.get(enabled, hovered), x, y, width * 2, height, 2, 256, 22, 0,0);
		RenderSystem.disableBlend();
	}

	@Dynamic
	@WrapWithCondition(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIFFIIII)V", ordinal = 1))
	private boolean disableUselessRendering(DrawContext instance, Identifier texture, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		return YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen);
	}

	@Dynamic
	@WrapWithCondition(method = "drawButtonRect", at = @At(value = "INVOKE", target = "Ldev/isxander/yacl3/gui/AbstractWidget;drawOutline(Lnet/minecraft/client/gui/DrawContext;IIIIII)V"))
	private boolean disableUselessRendering2(AbstractWidget instance, DrawContext graphics, int x1, int y1, int x2, int y2, int width, int color) {
		return YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen);
	}
	*///?}
}