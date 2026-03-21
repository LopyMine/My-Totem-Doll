package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.tooltip.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler implements Renderable, IRequestableTooltipScreen {

	@Final
	@Shadow
	public Font font;
	@Unique
	private TooltipRequest tooltipRequest;

	@Inject(at = @At("TAIL"), method = "extractRenderStateWithTooltipAndSubtitles")
	private void renderWithTooltip(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (this.tooltipRequest != null) {
			context.nextStratum();
			this.tooltipRequest.renderRenderState(context, mouseX, mouseY, delta);
			this.tooltipRequest = null;
		}
	}

	@Override
	public void myTotemDoll$requestTooltip(TooltipRequest tooltipRequest) {
		this.tooltipRequest = tooltipRequest;
	}

	@Override
	public TooltipRequest myTotemDoll$getCurrentRequest() {
		return this.tooltipRequest;
	}
}
