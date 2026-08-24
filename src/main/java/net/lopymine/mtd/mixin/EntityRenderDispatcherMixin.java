package net.lopymine.mtd.mixin;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	@Inject(at = @At("RETURN"), method = "extractEntity")
	private void checkEntity(Entity entity, float partialTicks, CallbackInfoReturnable<EntityRenderState> cir) {
		EntityRenderState value = cir.getReturnValue();

	}

}
