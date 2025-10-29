package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.*;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemPickupParticle.class)
public class ItemPickupParticleMixin {

	@Inject(at = @At("TAIL"), method = "<init>")
	private void markClear(ClientWorld world, EntityRenderState renderState, Entity collector, Vec3d velocity, CallbackInfo ci) {
		if (renderState instanceof ItemStackEntityRenderState state) {
			((ItemRenderStateWithStack) state.itemRenderState).myTotemDoll$shouldClear(false);
		}
	}

}
