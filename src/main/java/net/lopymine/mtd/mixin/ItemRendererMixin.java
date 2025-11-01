package net.lopymine.mtd.mixin;

//? if <=1.21.3 {

/*import com.llamalad7.mixinextras.sugar.Local;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.thing.ThingMarks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;

//? <=1.21.1
/^import net.minecraft.client.render.model.json.ModelTransformationMode;^/

import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.lopymine.mtd.utils.plugin.TotemDollPlugin;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Shadow
	@Final
	private ItemModels models;

	//? if >=1.21.2 {
	@Inject(at = @At(value = "HEAD"), method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;", cancellable = true)
	private void renderDoll(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
	//?} else {
	/^@Inject(at = @At(value = "HEAD"), method = "getModel", cancellable = true)
	private void renderDoll(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
	^///?}
		if (!MyTotemDollClient.canProcess(stack)) {
			return;
		}
		if (TotemDollPlugin.work(stack)) {
			BakedModel model = this.models/^? <=1.21.1 {^/ /^.getModelManager() ^//^?}^/.getModel(TotemDollPlugin.ID);
			stack.setModdedModel(true);
			cir.setReturnValue(model);
		}
	}


	//? if >=1.21.2 {
	@Inject(at = @At(value = "HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V", cancellable = true)
	private void renderDoll(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, boolean useInventoryModel, float z, CallbackInfo ci) {
	//?} else {
	/^@Inject(at = @At(value = "HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
	private void renderDoll(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
	^///?}
		DollRenderContext context = DollRenderContext.of(renderMode);
		if (TotemDollRenderer.sentRenderRequest(matrices, stack, context, light, overlay, 0, vertexConsumers)) {
			ci.cancel();
		}
	}

	@Inject(at = @At(value = "TAIL"), method = "renderItem*")
	private void disableModdedModel(CallbackInfo ci, @Local(argsOnly = true) ItemStack stack) {
		if (stack.hasModdedModel()) {
			stack.setModdedModel(false);
		}
	}

}

*///?}
