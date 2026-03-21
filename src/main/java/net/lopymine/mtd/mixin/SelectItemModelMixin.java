package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.renderer.item.SelectItemModel.ModelSelector;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(SelectItemModel.class)
public class SelectItemModelMixin {

	@Shadow
	@Final
	private ModelSelector<?> models;

	@Inject(at = @At("TAIL"), method = "update")
	private void markModdedIfModelChangedWithVanillaResourcePack(CallbackInfo ci, @Local ItemModel model, @Local(argsOnly = true) ItemStack stack) {
		this.checkModel(model, stack);
	}

	@Unique
	private void checkModel(ItemModel itemModel, ItemStack stack) {
		ItemModel standardModel = this.models.get(null, null);

		if (standardModel != itemModel) {
			stack.setModdedModel(true);
		}
	}

}
