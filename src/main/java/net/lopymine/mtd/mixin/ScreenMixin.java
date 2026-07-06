package net.lopymine.mtd.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import net.lopymine.mtd.utils.tooltip.TooltipRequest;
import net.lopymine.mtd.utils.tooltip.IRequestableTooltipScreen;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable, IRequestableTooltipScreen {

	@Shadow public Font font;
	@Unique
	private TooltipRequest myTotemDoll$tooltipRequest;

	@Inject(at = @At("TAIL"), method = "renderWithTooltip")
	private void renderWithTooltip(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.myTotemDoll$tooltipRequest != null) {
			this.myTotemDoll$tooltipRequest.render(context, mouseX, mouseY, delta);
			this.myTotemDoll$tooltipRequest = null;
		}
	}

	@Override
	public void myTotemDoll$requestTooltip(TooltipRequest tooltipRequest) {
		this.myTotemDoll$tooltipRequest = tooltipRequest;
	}

	@Override
	public TooltipRequest myTotemDoll$getCurrentRequest() {
		return this.myTotemDoll$tooltipRequest;
	}
}
