package net.lopymine.mtd.mixin.yacl;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.YACLScreen;
import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(YACLScreen.class)
public abstract class YACLScreenMixin extends Screen {

	@Shadow(remap = false)
	@Final
	public YetAnotherConfigLib config;

	protected YACLScreenMixin(Component title) {
		super(title);
	}

	@Shadow
	public abstract void onClose();

	@ModifyReturnValue(at = @At("RETURN"), method = "pendingChanges", remap = false)
	private boolean alwaysTrueBecauseYouCannotUseSaveButtonWithInstantOptionsImVerySadThatINeedThatDoYouAgreeWithMeYeahNoYepNopeWtf(boolean original) {
		if (YACLConfigurationScreen.notOpen(this)) {
			return original;
		}
		return true;
	}
}
