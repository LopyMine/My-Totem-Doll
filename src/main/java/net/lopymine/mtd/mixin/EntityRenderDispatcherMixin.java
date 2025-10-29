package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderManager.class)
public class EntityRenderDispatcherMixin {

	@ModifyReturnValue(at = @At(value = "RETURN"), method = "getAndUpdateRenderState")
	private EntityRenderState addStack(EntityRenderState original, @Local(argsOnly = true) Entity entity) {
//		if (entity instanceof ItemEntity itemEntity && original instanceof ItemStackEntityRenderState state) {
//			ItemStack stack = itemEntity.getStack();
//			if (MyTotemDollClient.canProcess(stack)) {
//				((ItemRenderStateWithStack) state.itemRenderState).myTotemDoll$setStack(stack);
//			}
//		}
		return original;
	}

}
