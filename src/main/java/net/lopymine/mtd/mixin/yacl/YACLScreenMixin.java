package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;


import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.lopymine.mtd.utils.mixin.yacl.CustomTabProvider;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;

@Pseudo
@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin extends Screen {

	@Dynamic
	@Shadow(remap = false)
	@Final
	public YetAnotherConfigLib config;
	@Dynamic
	@Shadow
	public ScreenRect tabArea;

	protected YACLScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	public abstract void close();

	@ModifyReturnValue(at = @At("RETURN"), method = "pendingChanges", remap = false)
	private boolean alwaysTrueBecauseYouCannotUseSaveButtonWithInstantOptionsImVerySadThatINeedThatDoYouAgreeWithMeYeahNoYepNopeWtf(boolean original) {
		if (YACLConfigurationScreen.notOpen(this)) {
			return original;
		}
		return true;
	}

	@Inject(at = @At(value = "HEAD"), method = /*? if =1.20.1 {*/ /*"lambda$init$4" *//*?} else {*/ "lambda$init$0" /*?}*/, remap = false, cancellable = true)
	private void addCustomTabProviding(ConfigCategory category, CallbackInfoReturnable<TabExt> cir) {
		if (category instanceof CustomTabProvider customTabProvider) {
			YACLScreen screen = (YACLScreen) (Object) this;
			cir.setReturnValue(customTabProvider.createTab(screen, this.tabArea));
		}
	}
}
