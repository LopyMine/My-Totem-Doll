package net.lopymine.mtd.mixin.yacl.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.gui.BackgroundRenderer;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget implements Renderable {

	public EditBoxMixin(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@Shadow
	protected abstract boolean isEditable();

	@WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;isBordered()Z"))
	private boolean wrapBackgroundRendering(EditBox instance, Operation<Boolean> original, @Local(argsOnly = true) GuiGraphics context) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			return original.call(instance);
		}
		BackgroundRenderer.drawTransparencyWidgetBackground(context, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isEditable() && this.active, this.isHoveredOrFocused());
		return false;
	}
}
