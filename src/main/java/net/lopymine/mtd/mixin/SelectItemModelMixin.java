package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.item.SelectItemModel.ModelSelector;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(SelectItemModel.class)
public abstract class SelectItemModelMixin<T> implements ItemModel {

	@Shadow
	@Final
	private ModelSelector<?> models;

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/SelectItemModel$ModelSelector;get(Ljava/lang/Object;Lnet/minecraft/client/multiplayer/ClientLevel;)Lnet/minecraft/client/renderer/item/ItemModel;"), method = "update")
	private ItemModel markModdedIfModelChangedWithVanillaResourcePack(ModelSelector<T> instance, @Nullable T value, @Nullable ClientLevel clientLevel, Operation<ItemModel> original, @Local(argsOnly = true) ItemStack itemStack) {
		ItemModel model = original.call(instance, value, clientLevel);
		this.checkModel(model, itemStack, value);
		return model;
	}

	@Unique
	private void checkModel(ItemModel itemModel, ItemStack stack, T value) {
		if (!(value instanceof Component)) {
			return;
		}

		ItemModel standardModel = this.models.get(null, null);
		if (standardModel != itemModel && stack.getRealCustomName() != null) {
			stack.setModdedModel(true);
		}
	}

}
