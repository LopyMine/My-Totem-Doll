package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.mixin.ItemStackWithModdedBakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;

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

}
