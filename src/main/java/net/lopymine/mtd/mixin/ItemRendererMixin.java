package net.lopymine.mtd.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
//? if neoforge {
/*import net.minecraft.client.resources.model.ModelResourceLocation;
*///?}
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Shadow
	@Final
	private ItemModelShaper itemModelShaper;

	@Inject(at = @At(value = "HEAD"), method = "getModel", cancellable = true)
	private void renderDoll(ItemStack stack, Level world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return;
		}
		if (TotemDollPlugin.work(stack)) {
			BakedModel model = this.itemModelShaper.getModelManager().getModel(/*? if fabric {*/ TotemDollPlugin.ID /*?} else {*/ /*ModelResourceLocation.standalone(TotemDollPlugin.ID)*//*?}*/);
			stack.setModdedModel(true);
			cir.setReturnValue(model);
		}
	}


	@Inject(at = @At(value = "HEAD"), method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", cancellable = true)
	private void renderDoll(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		DollRenderContext context = DollRenderContext.of(renderMode);
		if (TotemDollRenderer.sentRenderRequest(matrices, stack, context, light, overlay, 0, vertexConsumers)) {
			ci.cancel();
		}
	}

	@Inject(at = @At(value = "TAIL"), method = "renderStatic*")
	private void disableModdedModel(CallbackInfo ci, @Local(argsOnly = true) ItemStack stack) {
		if (stack.hasModdedModel()) {
			stack.setModdedModel(false);
		}
	}

}

