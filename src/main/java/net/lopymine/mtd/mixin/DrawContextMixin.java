package net.lopymine.mtd.mixin;

//? if >=1.21.6 {

import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.doll.renderer.special.TotemDollRenderState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.DrawContext.ScissorStack;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(DrawContext.class)
public class DrawContextMixin {

	@Shadow
	@Final
	public GuiRenderState state;

	@Shadow
	@Final
	public ScissorStack scissorStack;

	@Shadow
	@Final
	private Matrix3x2fStack matrices;

	//? if >=1.21.9 {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/item/ItemModelManager;clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/util/HeldItemContext;I)V",
					shift = Shift.AFTER
			),
			method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V",
			cancellable = true
	)
	private void swapTotemRendering(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
		this.renderDoll(stack, x, y, ci);
	}
	//?} else {
	/*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/item/ItemModelManager;clearAndUpdate(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V", shift = Shift.AFTER), method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V", cancellable = true)
	private void swapTotemRendering(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
		this.renderDoll(stack, x, y, ci);
	}
	*///?}

	@Unique
	private void renderDoll(ItemStack stack, int x, int y, CallbackInfo ci) {
		if (!TotemDollRenderer.canRender(stack)) {
			return;
		}
		this.state.addSpecialElement(TotemDollRenderState.getGui(stack, x, y, new Matrix3x2f(this.matrices), this.scissorStack.peekLast()));
		ci.cancel();
	}

}
//?}
