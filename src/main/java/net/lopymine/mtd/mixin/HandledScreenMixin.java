package net.lopymine.mtd.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.lopymine.mtd.gui.widget.tag.*;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;

//? if >=1.21.9 {
import net.minecraft.client.gui.Click;
//?}

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

	//? if >=1.21.9 {
	@Inject(at = @At("HEAD"), method = "mouseDragged", cancellable = true)
	private void mouseDragged(Click click, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null){
			return;
		}
		if (tagButtonWidget.mouseDragged(click, deltaX, deltaY)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "mouseReleased", cancellable = true)
	private void mouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null){
			return;
		}
		if (tagButtonWidget.mouseReleased(click)) {
			cir.setReturnValue(true);
		}
	}
	//?} else {
	/*@Inject(at = @At("HEAD"), method = "mouseDragged", cancellable = true)
	private void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null){
			return;
		}
		if (tagButtonWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = @At("HEAD"), method = "mouseReleased", cancellable = true)
	private void mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		TagButtonWidget tagButtonWidget = this.getTagButtonWidget();
		if (tagButtonWidget == null){
			return;
		}
		if (tagButtonWidget.mouseReleased(mouseX, mouseY, button)) {
			cir.setReturnValue(true);
		}
	}
	*///?}

	//? if >=1.21.2 {
	@Inject(at = @At("HEAD"), method = "mouseScrolled", cancellable = true)
	private void onMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
		TagMenuWidget tagMenuWidget = this.getTagMenuWidget();
		if (tagMenuWidget == null) {
			return;
		}
		if (tagMenuWidget.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
			cir.setReturnValue(true);
		}
	}
	//?}

	@Unique
	private @Nullable TagMenuWidget getTagMenuWidget() {
		if (!(this instanceof MTDAnvilScreen anvilScreen)) {
			return null;
		}
		return anvilScreen.myTotemDoll$getTagMenuWidget();
	}

	@Unique
	private @Nullable TagButtonWidget getTagButtonWidget() {
		if (!(this instanceof MTDAnvilScreen anvilScreen)) {
			return null;
		}
		return anvilScreen.myTotemDoll$getTagButtonWidget();
	}
}
