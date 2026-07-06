package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.YetAnotherConfigLib.Builder;
import dev.isxander.yacl3.impl.YetAnotherConfigLibImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.utils.mixin.yacl.*;

@SuppressWarnings("UnstableApiUsage")
@Mixin(YetAnotherConfigLibImpl.BuilderImpl.class)
public class YetAnotherConfigLibImplBuilderMixin implements BetterYACLScreenBuilder {

	@Unique
	private boolean myTotemDoll$enabled;

	@ModifyReturnValue(at = @At("RETURN"), method = "build", remap = false)
	private YetAnotherConfigLib swapScreen(YetAnotherConfigLib original) {
		if (!this.myTotemDoll$enabled) {
			return original;
		}
		return ((BetterYACLScreenConfig) original).myTotemDoll$enable();
	}

	@Override
	public Builder myTotemDoll$enable() {
		this.myTotemDoll$enabled = true;
		return ((Builder) this);
	}
}
