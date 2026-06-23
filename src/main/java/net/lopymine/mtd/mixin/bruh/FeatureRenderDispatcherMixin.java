package net.lopymine.mtd.mixin.bruh;

import net.lopymine.mtd.bruh.TotemDollFeatureRenderer;
import net.minecraft.client.renderer.feature.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class FeatureRenderDispatcherMixin {

	@Shadow @Final private FeatureRendererMap featureRenderers;

	@Inject(at = @At("TAIL"), method = "<init>")
	private void registerTotemDollRenderer(CallbackInfo ci) {
		this.featureRenderers.put(TotemDollFeatureRenderer.TYPE, new TotemDollFeatureRenderer());
	}

}
