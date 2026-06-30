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
	private ItemStack myTotemDoll$stack;

	@Unique
	private boolean myTotemDoll$shouldClear = true;

	@Inject(at = @At("HEAD"), method = "submit", cancellable = true)
	private void renderRenderState(PoseStack matrices, SubmitNodeCollector collector, int light, int overlay, int outlineColor, CallbackInfo ci) {
		DollRenderContext context = DollRenderContext.of(this.displayContext);

		if (this.myTotemDoll$stack != null) {
			if (TotemDollRenderer.submit(collector, matrices, this.myTotemDoll$stack, context, light, overlay, outlineColor)) {
				ci.cancel();
			}
		}

		if (this.myTotemDoll$shouldClear) {
			if (this.myTotemDoll$stack != null && this.myTotemDoll$stack.hasModdedModel()) {
				this.myTotemDoll$stack.setModdedModel(false);
			}
			this.myTotemDoll$stack = null;
		}
	}

	@Override
	public void myTotemDoll$setStack(ItemStack stack) {
		this.myTotemDoll$stack = stack;
	}

	@Override
	public void myTotemDoll$shouldClear(boolean bl) {
		this.myTotemDoll$shouldClear = bl;
	}

	@Override
	public void myTotemDoll$reset() {
		this.myTotemDoll$stack       = null;
		this.myTotemDoll$shouldClear = false;
	}
}

