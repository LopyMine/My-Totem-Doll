package net.lopymine.mtd.mixin.yacl.widget;

import dev.isxander.yacl3.gui.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionDescriptionWidget.class)
public class OptionDescriptionWidgetMixin {

	@Shadow(remap = false)
	private float currentScrollAmount;

	@Shadow(remap = false)
	private float targetScrollAmount;

	@Shadow(remap = false)
	private int maxScrollAmount;

	@Unique
	private float myTotemDoll$currentScroll, myTotemDoll$targetScroll;

	@Inject(at = @At("HEAD"), method = "setOptionDescription", remap = false)
	private void saveScroll(DescriptionWithName description, CallbackInfo ci) {
		this.myTotemDoll$currentScroll = this.currentScrollAmount;
		this.myTotemDoll$targetScroll  = this.targetScrollAmount;
	}

	@Inject(at = @At("TAIL"), method = "setOptionDescription", remap = false)
	private void loadScroll(DescriptionWithName description, CallbackInfo ci) {
		this.currentScrollAmount = this.myTotemDoll$currentScroll;
		this.targetScrollAmount  = this.myTotemDoll$targetScroll;
	}

	@Inject(at = @At("TAIL"), method = "extractWidgetRenderState")
	private void fixMaxScroll(CallbackInfo ci) {
		if (this.targetScrollAmount > this.maxScrollAmount) {
			this.targetScrollAmount = this.maxScrollAmount;
		}
	}
}
