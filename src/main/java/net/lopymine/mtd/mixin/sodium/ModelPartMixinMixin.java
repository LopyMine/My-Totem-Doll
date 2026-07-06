package net.lopymine.mtd.mixin.sodium;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.client.model.geom.ModelPart;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.model.base.*;

@Pseudo
@Mixin(value = ModelPart.class, priority = 1500)
public class ModelPartMixinMixin {

	@Dynamic
	@TargetHandler(
			mixin = "me.jellysquid.mods.sodium.mixin.features.render.entity.ModelPartMixin",
			name = "onRender",
			prefix = "handler"
	)
	@Inject(at = @At("HEAD"), method = "@MixinSquared:Handler", cancellable = true, remap = false, require = 0)
	private void helloSodium(PoseStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo a, CallbackInfo b) {
		ModelPart modelPart = (ModelPart) (Object) this;
		if (!(modelPart instanceof MModel)) {
			return;
		}
		b.cancel();
	}

}