package net.lopymine.mtd.mixin;

import net.lopymine.mtd.queue.TotemDollWorldRenderRequestsCollector;
import net.lopymine.mtd.thing.ThingMarks;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class GameRendererQueueMixin {

	@Inject(at = @At("HEAD"), method = "renderEntities")
	private void beforeDollRendering(CallbackInfo ci) {
		ThingMarks.WORLD_RENDERING.get().setMarked(true);
	}

	@Inject(at = @At("TAIL"), method = "renderEntities")
	private void afterDollRendering(CallbackInfo ci) {
		TotemDollWorldRenderRequestsCollector.getInstance().render();
		ThingMarks.WORLD_RENDERING.get().setMarked(false);
	}

}
