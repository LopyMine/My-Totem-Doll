package net.lopymine.mtd.mixin;

import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.doll.renderer.special.TotemDollRenderState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphics.ScissorStack;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

	@Shadow
	@Final
	public GuiRenderState guiRenderState;

	@Shadow
	@Final
	public ScissorStack scissorStack;

	@Shadow
	@Final
	private Matrix3x2fStack pose;

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/item/ItemModelResolver;updateForTopItem(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/ItemOwner;I)V",
					shift = Shift.AFTER
			),
			method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
			cancellable = true
	)
	private void swapTotemRendering(LivingEntity entity, Level world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
		this.renderDoll(stack, x, y, ci);
	}

	@Unique
	private void renderDoll(ItemStack stack, int x, int y, CallbackInfo ci) {
		if (!TotemDollRenderer.canRender(stack)) {
			return;
		}
		this.guiRenderState.submitPicturesInPictureState(TotemDollRenderState.getGui(stack, x, y, new Matrix3x2f(this.pose), this.scissorStack.peek()));
		ci.cancel();
	}

}
