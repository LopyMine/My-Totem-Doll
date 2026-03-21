package net.lopymine.mtd.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.gui.BackgroundRenderer;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget implements Renderable {

	public AbstractButtonMixin(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@WrapOperation(method = "extractDefaultSprite", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V"))
	private void renderTransparencyWidget(GuiGraphicsExtractor instance, com.mojang.blaze3d.pipeline.RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(instance, pipeline, sprite, x, y, width, height, color);
			return;
		}
		BackgroundRenderer.drawTransparencyWidgetBackground(instance, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.active, this.isHoveredOrFocused());
	}
}