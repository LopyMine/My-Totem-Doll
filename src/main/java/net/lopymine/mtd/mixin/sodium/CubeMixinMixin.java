package net.lopymine.mtd.mixin.sodium;

import com.bawnorton.mixinsquared.TargetHandler;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lopymine.mtd.model.base.MCuboid;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(Cube.class)
public class CubeMixinMixin {

	@Dynamic
	@TargetHandler(
			mixin = "net.caffeinemc.mods.sodium.mixin.features.render.entity.CubeMixin",
			name = "onCompile",
			prefix = "handler"
	)
	@Inject(method = "@MixinSquared:Handler", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void helloSodium(Pose pose, VertexConsumer buffer, int light, int overlay, int color, CallbackInfo a, CallbackInfo b) {
		Cube cuboid = (Cube) (Object) this;
		if (!(cuboid instanceof MCuboid)) {
			return;
		}
		b.cancel();
	}

}
