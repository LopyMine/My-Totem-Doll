package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.lopymine.mtd.modmenu.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.modmenu.yacl.custom.category.dummy.DummyCategoryTab;
import net.lopymine.mtd.utils.mixin.*;
import net.lopymine.mtd.utils.mixin.CustomTabProvider;

@Pseudo
@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin extends Screen  {

	@Dynamic
	@Shadow(remap = false)
	@Final
	public YetAnotherConfigLib config;

	protected YACLScreenMixin(Text title) {
		super(title);
	}

	@Dynamic
	@Shadow
	public abstract void close();

	@Dynamic
	@Shadow public ScreenRect tabArea;

	@Dynamic
	@ModifyReturnValue(at = @At("RETURN"), method = "pendingChanges", remap = false)
	private boolean alwaysTrueBecauseYouCannotUseSaveButtonWithInstantOptionsImVerySadThatINeedToDoThatDoYouAgreeWithMeYeahNoYepNopeWtf(boolean original) {
		if (YACLConfigurationScreen.notOpen(this)) {
			return original;
		}
		return true;
	}

	@Unique
	private static final String LAMBDA_IN_INIT = /*? >=1.20.3 || =1.20.1 {*/ "lambda$init$4" /*?} else {*/ /*"lambda$init$2" *//*?}*/;

	@Dynamic
	@Inject(at = @At(value = "HEAD"), method = LAMBDA_IN_INIT, remap = false, cancellable = true)
	private void addCustomTabProviding(ConfigCategory category, CallbackInfoReturnable<TabExt> cir) {
		if (category instanceof CustomTabProvider customTabProvider) {
			YACLScreen screen = (YACLScreen) (Object) this;
			cir.setReturnValue(customTabProvider.createTab(screen, this.tabArea));
		}
	}

	//? if (=1.20.2 || =1.20.3) {

	/*@WrapOperation(at = @At(value = "NEW", target = "(IIII)Lnet/minecraft/client/gui/ScreenRect;"), method = "init")
	private ScreenRect cancelOldTabArea(int sameAxis, int otherAxis, int width, int height, Operation<ScreenRect> original) {
		if (YACLConfigurationScreen.notOpen(this)) {
			original.call(sameAxis, otherAxis, width, height);
		}
		return this.tabArea;
	}

	*///?}
}
