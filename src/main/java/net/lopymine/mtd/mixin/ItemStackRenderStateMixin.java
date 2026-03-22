package net.lopymine.mtd.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin implements ItemRenderStateWithStack {

	@Shadow
	ItemDisplayContext displayContext;

	@Unique
	@Nullable
	private ItemStack stack;

	@Unique
	private boolean shouldClear = true;

	@Inject(at = @At("HEAD"), method = "submit", cancellable = true)
	private void renderRenderState(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int outlineColor, CallbackInfo ci) {
		this.renderDoll(matrices, light, overlay, outlineColor, null, ci);
	}

	@Unique
	private void renderDoll(PoseStack matrices, int light, int overlay, @SuppressWarnings("all") int outlineColor, @Nullable MultiBufferSource provider, CallbackInfo ci) {
		DollRenderContext context = DollRenderContext.of(this.displayContext);

		if (this.stack != null) {
			if (TotemDollRenderer.sentRenderRequest(matrices, this.stack, context, light, overlay, outlineColor, provider)) {
				ci.cancel();
			}
		}

		if (this.shouldClear) {
			if (this.stack != null && this.stack.hasModdedModel()) {
				this.stack.setModdedModel(false);
			}
			this.stack = null;
		}
	}

	@Override
	public void myTotemDoll$setStack(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public void myTotemDoll$shouldClear(boolean bl) {
		this.shouldClear = bl;
	}

	@Override
	public void myTotemDoll$reset() {
		this.stack = null;
		this.shouldClear = false;
	}
}

