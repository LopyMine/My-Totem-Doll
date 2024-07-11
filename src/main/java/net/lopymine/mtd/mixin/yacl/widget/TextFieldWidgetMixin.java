package net.lopymine.mtd.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
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
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends ClickableWidget implements Drawable {

	public TextFieldWidgetMixin(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@Dynamic
	@Shadow
	protected abstract boolean isEditable();

	@Unique
	private static final String RENDER_METHOD = /*? >=1.20.3 {*/ "renderWidget" /*?} else {*/ /*"renderButton" *//*?}*/;

	//? if >=1.20.2 {

	@Dynamic
	@WrapOperation(method = RENDER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void renderTransparencyWidget1(DrawContext instance, Identifier identifier, int x, int y, int width, int height, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			original.call(instance, identifier, x, y, width, height);
			return;
		}

		RenderSystem.enableBlend();
		instance.drawGuiTexture(TransparencySprites.WIDGET_SPRITES.get(this.isEditable() && this.active, this.isSelected()), x, y, width, height);
		RenderSystem.disableBlend();
	}

	//?} else {

	/*@Dynamic
	@WrapOperation(method = RENDER_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;drawsBackground()Z"))
	private boolean wrapBackgroundRendering(TextFieldWidget instance, Operation<Boolean> original, @Local(argsOnly = true) DrawContext context) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			return original.call(instance);
		}
		RenderSystem.enableBlend();
		context.drawNineSlicedTexture(TransparencySprites.WIDGET_SPRITES.get(this.isEditable() && this.active, this.isSelected()), this.getX(), this.getY() , this.getWidth(), this.getHeight(), 2, 256, 22, 0,0);
		RenderSystem.disableBlend();
		return false;
	}
	*///?}
}
