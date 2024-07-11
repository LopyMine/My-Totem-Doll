package net.lopymine.mtd.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.render.DollRenderer;
import net.lopymine.mtd.mixin.accessor.BuiltinModelItemRendererAccessor;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Shadow
	@Final
	private BuiltinModelItemRenderer builtinModelItemRenderer;

	@Inject(at = @At(value = "HEAD"), method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", cancellable = true)
	private void renderDoll(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
		if (MyTotemDollClient.getConfig().isModEnabled() && stack.isOf(Items.TOTEM_OF_UNDYING)) {
			DollRenderer.renderDoll(matrices, ((BuiltinModelItemRendererAccessor) this.builtinModelItemRenderer).getEntityModelLoader(), stack, renderMode, leftHanded, vertexConsumers, light, overlay, model);
			ci.cancel();
		}
	}
}
