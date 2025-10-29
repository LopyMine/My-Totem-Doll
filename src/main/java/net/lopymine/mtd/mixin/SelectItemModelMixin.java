package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.render.item.model.*;
import net.minecraft.client.render.item.model.SelectItemModel.ModelSelector;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(SelectItemModel.class)
public class SelectItemModelMixin {

	@Shadow @Final private ModelSelector<?> selector;

	@Inject(at = @At("TAIL"), method = "update")
	private void markModdedIfModelChangedWithVanillaResourcePack(CallbackInfo ci, @Local ItemModel model, @Local(argsOnly = true) ItemStack stack) {
		this.checkModel(model, stack);
	}

	@Unique
	private void checkModel(ItemModel itemModel, ItemStack stack) {
		ItemModel standardModel = this.selector.get(null, null);
		if (standardModel != itemModel) {
			stack.setModdedModel(true);
		}
	}

}
