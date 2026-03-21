package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.impl.YetAnotherConfigLibImpl;
import net.lopymine.mtd.utils.mixin.yacl.BetterYACLScreenConfig;
import net.lopymine.mtd.yacl.custom.screen.MyTotemDollYACLScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(YetAnotherConfigLibImpl.class)
public class YetAnotherConfigLibImplMixin implements BetterYACLScreenConfig {

	@Unique
	private boolean enabled;


	@ModifyReturnValue(at = @At("RETURN"), method = "generateScreen")
	private Screen swapScreen(Screen original, @Local(argsOnly = true) Screen parent) {
		if (!this.enabled) {
			return original;
		}
		return new MyTotemDollYACLScreen(((YetAnotherConfigLib) this), parent);
	}


	@Override
	public YetAnotherConfigLib myTotemDoll$enable() {
		this.enabled = true;
		return ((YetAnotherConfigLib) this);
	}
}
