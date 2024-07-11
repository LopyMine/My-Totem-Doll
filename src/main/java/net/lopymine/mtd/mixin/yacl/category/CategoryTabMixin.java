package net.lopymine.mtd.mixin.yacl.category;

import dev.isxander.yacl3.gui.*;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.modmenu.yacl.YACLConfigurationScreen;

@Pseudo
@Mixin(value = YACLScreen.CategoryTab.class)
public class CategoryTabMixin {

	//? if >=1.20.4 || =1.20.1 {

	@Dynamic
	@Shadow
	@Final
	public ButtonWidget undoButton;

	@Dynamic
	@Shadow
	@Final
	public ButtonWidget saveFinishedButton;

	@Dynamic
	@Shadow
	@Final
	public ButtonWidget cancelResetButton;

	@Dynamic
	@Inject(at = @At("HEAD"), method = "updateButtons", cancellable = true, remap = false)
	private void changeDefaultButtonTexts(CallbackInfo ci) {
		if (YACLConfigurationScreen.notOpen(MinecraftClient.getInstance().currentScreen)) {
			return;
		}
		this.undoButton.active = false;
		this.saveFinishedButton.setMessage(GuiUtils.translatableFallback("yacl.gui.done", ScreenTexts.DONE));
		this.saveFinishedButton.setTooltip(new YACLTooltip(Text.translatable("yacl.gui.finished.tooltip"), this.saveFinishedButton));
		this.cancelResetButton.setMessage(Text.translatable("controls.reset"));
		this.cancelResetButton.setTooltip(new YACLTooltip(Text.translatable("yacl.gui.reset.tooltip"), this.cancelResetButton));
		ci.cancel();
	}

	//?}

}
