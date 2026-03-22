package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.doll.renderer.special.TotemDollRenderState;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiGraphicsExtractor.ScissorStack;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(GuiGraphicsExtractor.class)
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
			method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
			cancellable = true
	)
	private void swapTotemRendering(LivingEntity entity, Level world, ItemStack stack, int x, int y, int seed, CallbackInfo ci, @Local TrackingItemStackRenderState state) {
		if (!this.renderDoll(stack, x, y, ci) && state instanceof ItemRenderStateWithStack stateWithStack) {
			stateWithStack.myTotemDoll$reset();
		}
	}

	@Unique
	private boolean renderDoll(ItemStack stack, int x, int y, CallbackInfo ci) {
		if (!TotemDollRenderer.canRender(stack)) {
			return false;
		}
		this.guiRenderState.addPicturesInPictureState(TotemDollRenderState.getGui(stack, x, y, new Matrix3x2f(this.pose), this.scissorStack.peek()));
		ci.cancel();
		return true;
	}

}
