package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;

@Pseudo
@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin extends Screen {

	@Dynamic
	@Shadow(remap = false)
	@Final
	public YetAnotherConfigLib config;

	protected YACLScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	public abstract void close();

	@Dynamic
	@ModifyReturnValue(at = @At("RETURN"), method = "pendingChanges", remap = false)
	private boolean alwaysTrueBecauseYouCannotUseSaveButtonWithInstantOptionsImVerySadThatINeedThatDoYouAgreeWithMeYeahNoYepNopeWtf(boolean original) {
		if (YACLConfigurationScreen.notOpen(this)) {
			return original;
		}
		return true;
	}
}
