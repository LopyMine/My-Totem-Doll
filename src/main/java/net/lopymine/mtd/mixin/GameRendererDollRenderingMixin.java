package net.lopymine.mtd.mixin;

import net.lopymine.mtd.optimization.TotemDollRenderRequestsCollector;
import net.lopymine.mtd.thing.ThingMarks;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class GameRendererDollRenderingMixin {

	@Inject(at = @At("HEAD"), method = "extractVisibleEntities")
	private void beforeDollRendering(CallbackInfo ci) {
		ThingMarks.WORLD_RENDERING.get().setMarked(true);
	}

	@Inject(at = @At("TAIL"), method = "extractVisibleEntities")
	private void afterDollRendering(CallbackInfo ci) {
		TotemDollRenderRequestsCollector.getInstance().renderStates();
		ThingMarks.WORLD_RENDERING.get().setMarked(false);
	}

}
