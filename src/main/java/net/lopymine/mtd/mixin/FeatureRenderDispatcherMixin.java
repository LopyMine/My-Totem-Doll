package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.mtd.renderer.TotemDollFeatureRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class FeatureRenderDispatcherMixin {

	@Shadow @Final private BufferSource bufferSource;

	@Shadow @Final private OutlineBufferSource outlineBufferSource;

	@Unique
	private final TotemDollFeatureRenderer myTotemDoll$totemDollFeatureRenderer = new TotemDollFeatureRenderer();

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/feature/ModelPartFeatureRenderer;renderTranslucent(Lnet/minecraft/client/renderer/SubmitNodeCollection;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"
			),
			method = "renderTranslucentFeatures"
	)
	private void renderTotemDolls(CallbackInfo ci, @Local SubmitNodeCollection collection) {
		this.myTotemDoll$totemDollFeatureRenderer.render(collection, this.bufferSource, this.outlineBufferSource);
	}

}
