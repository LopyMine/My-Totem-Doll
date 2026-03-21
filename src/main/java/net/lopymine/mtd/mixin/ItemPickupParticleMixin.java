package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.renderer.entity.state.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemPickupParticle.class)
public class ItemPickupParticleMixin {

	@Inject(at = @At("TAIL"), method = "<init>")
	private void markClear(ClientLevel world, EntityRenderState renderState, Entity collector, Vec3 velocity, CallbackInfo ci) {
		if (renderState instanceof ItemClusterRenderState state) {
			((ItemRenderStateWithStack) state.item).myTotemDoll$shouldClear(false);
		}
	}

}
