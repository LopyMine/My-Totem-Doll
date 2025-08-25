package net.lopymine.mtd.mixin;

//? if >=1.21.4 {
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.thing.ThingMarks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;

import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(ItemRenderState.class)
public class ItemRenderStateMixin implements ItemRenderStateWithStack {

	//? if <=1.21.4 {
	/*@Shadow
	ModelTransformationMode modelTransformationMode;
	@Shadow
	boolean leftHand;
	*///?} else {
	@Shadow ItemDisplayContext displayContext;
	//?}

	@Unique
	@Nullable
	private ItemStack stack;

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
		DollRenderContext context = DollRenderContext.of(/*? if <=1.21.4 {*//*this.modelTransformationMode*//*?} else {*/ this.displayContext /*?}*/);

		if (this.stack != null) {
			if (ThingMarks.WORLD_RENDERING.get().isMarked() && TotemDollRenderer.sentRenderRequest(matrices, this.stack, context, vertexConsumers, light, overlay)) {
				ci.cancel();
			} else
				if (TotemDollRenderer.rendered(matrices, this.stack, context, vertexConsumers, light, overlay)) {
				ci.cancel();
			}
		}

		if (this.stack != null && this.stack.hasModdedModel()) {
			this.stack.setModdedModel(false);
		}

		this.stack = null;
	}

	@Override
	public void myTotemDoll$setStack(ItemStack stack) {
		this.stack = stack;
	}
}

//?}