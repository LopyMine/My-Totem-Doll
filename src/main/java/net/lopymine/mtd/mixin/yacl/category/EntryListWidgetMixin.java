package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.modmenu.yacl.*;

@Pseudo
@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin /*? >=1.20.3 {*/ extends ClickableWidget /*?}*/ {

	//? if >=1.20.3 {
	public EntryListWidgetMixin(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}
	//?}

	//? if >=1.20.5 {

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0), method = "renderWidget")
	private void renderTransparencyScrollerBackground(DrawContext context, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, texture, x, y, width, height);
			return;
		}
		original.call(context, TransparencySprites.SCROLLER_BACKGROUND_SPRITE, x, y, width, height);
	}

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 1), method = "renderWidget")
	private void renderTransparencyScroller(DrawContext context, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, texture, x, y, width, height);
			return;
		}
		original.call(context, TransparencySprites.SCROLLER_SPRITE, x, y, width, height);
	}

	//?} else {

	/*@Unique
	private static final String RENDER_METHOD = /^? >=1.20.3 {^/ "renderWidget" /^?} else {^/ "render" /^?}^/;

	//? <=1.20.2 {
	@Shadow
	protected int bottom;
	@Shadow
	protected int top;
	@Shadow
	protected int width;
	@Shadow
	protected int height;

	//?}

	@Shadow
	protected abstract int getScrollbarPositionX();

	//? if <=1.20.1 {

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0), method = "render")
	private void renderTransparencyScrollerBackground1(DrawContext context, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, x1, y1, x2, y2, color);
			return;
		}
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.drawNineSlicedTexture(TransparencySprites.SCROLLER_BACKGROUND_SPRITE, this.getScrollbarPositionX(), this.top, 6, this.height, 1, 6, 32, 0, 0);
	}

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 1), method = "render")
	private void renderTransparencyScroller2(DrawContext context, int x, int y, int width, int height, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, x, y, width, height, color);
			return;
		}

		context.drawNineSlicedTexture(TransparencySprites.SCROLLER_SPRITE, this.getScrollbarPositionX(), y, 6, height - y, 1, 6, 32, 0, 0);
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}

	@Dynamic
	@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 2), method = "render")
	private boolean renderTransparencyScrollerBackground3(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
		return YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen);
	}

	//?} else {

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0), method = RENDER_METHOD)
	private void renderTransparencyScrollerBackground(DrawContext context, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, x1, y1, x2, y2, color);
			return;
		}
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.drawGuiTexture(TransparencySprites.SCROLLER_BACKGROUND_SPRITE, this.getScrollbarPositionX(), /^? >=1.20.3 {^/ this.getY() /^?} else {^/this.top /^?}^/, 6, /^? >=1.20.3 {^/ this.getBottom() /^?} else {^/ this.bottom /^?}^/);
	}

	@Dynamic
	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0), method = RENDER_METHOD)
	private void renderTransparencyScroller(DrawContext context, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(context, texture, x, y, width, height);
			return;
		}

		context.drawGuiTexture(TransparencySprites.SCROLLER_SPRITE, x, y, width, height);
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}

	//?}

	@Dynamic
	@ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderBackground:Z"), method = RENDER_METHOD)
	private boolean disableBackgroundRendering(boolean original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			return original;
		}
		return false;
	}

	//? if <=1.20.1 {

	@Dynamic
	@ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;renderHorizontalShadows:Z"), method = "render")
	private boolean disableShadows(boolean original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			return original;
		}
		return false;
	}

	//?}

	*///?}
}
