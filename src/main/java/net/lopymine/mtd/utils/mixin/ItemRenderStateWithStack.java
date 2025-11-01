package net.lopymine.mtd.utils.mixin;

import net.minecraft.item.ItemStack;

public interface ItemRenderStateWithStack {

	void myTotemDoll$setStack(ItemStack stack);

	void myTotemDoll$shouldClear(boolean bl);

}
