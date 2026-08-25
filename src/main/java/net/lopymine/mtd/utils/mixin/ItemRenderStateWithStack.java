package net.lopymine.mtd.utils.mixin;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemRenderStateWithStack {

	void myTotemDoll$setStack(ItemStack stack);

	void myTotemDoll$setSourceId(@Nullable String sourceId);

	@Nullable
	String myTotemDoll$getSourceId();

	void myTotemDoll$shouldClear(boolean bl);

	void myTotemDoll$reset();

}
