package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.item.SelectItemModel.ModelSelector;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(SelectItemModel.class)
public class SelectItemModelMixin {

	@Shadow
	@Final
	private ModelSelector<?> models;

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemModel;update(Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/item/ItemModelResolver;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/entity/ItemOwner;I)V"), method = "update")
	private void markModdedIfModelChangedWithVanillaResourcePack(ItemModel instance, ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, ClientLevel clientLevel, ItemOwner itemOwner, int i, Operation<Void> original) {
		this.checkModel(instance, itemStack);
		original.call(instance, itemStackRenderState, itemStack, itemModelResolver, itemDisplayContext, clientLevel, itemOwner, i);
	}

	@Unique
	private void checkModel(ItemModel itemModel, ItemStack stack) {
		ItemModel standardModel = this.models.get(null, null);

		if (standardModel != itemModel) {
			stack.setModdedModel(true);
		}
	}

}
