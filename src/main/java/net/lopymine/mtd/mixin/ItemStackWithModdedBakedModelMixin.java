package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.mixin.ItemStackWithModdedBakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackWithModdedBakedModelMixin implements ItemStackWithModdedBakedModel {

	@Unique
	private boolean myTotemDoll$modded = false;

	@Override
	public void myTotemDoll$setModdedModel(boolean modded) {
		this.myTotemDoll$modded = modded;
	}

	@Override
	public boolean myTotemDoll$isModdedModel() {
		return myTotemDoll$modded;
	}

	@Inject(at = @At("RETURN"), method = "copy")
	private void markItemStack(CallbackInfoReturnable<ItemStack> cir) {
		((ItemStackWithModdedBakedModel) cir.getReturnValue()).myTotemDoll$setModdedModel(this.myTotemDoll$isModdedModel());
		this.myTotemDoll$modded = false;
	}
}
