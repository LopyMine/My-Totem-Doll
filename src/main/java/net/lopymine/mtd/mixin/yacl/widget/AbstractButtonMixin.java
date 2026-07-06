package net.lopymine.mtd.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.gui.BackgroundRenderer;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;

import java.util.function.Function;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget implements Renderable {

	public AbstractButtonMixin(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"))
	private void renderTransparencyWidget1(GuiGraphics context, ResourceLocation identifier, int x, int y, int w, int h, int a, int b, int c, int d, int e, int i, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(context, identifier, x, y, w, h, a, b, c, d, e, i);
			return;
		}
		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, w, h, this.active, this.isHoveredOrFocused());
	}
}