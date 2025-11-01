package net.lopymine.mtd.mixin;

//? if >=1.21.4 {

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.render.item.model.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21.5 {

import net.minecraft.client.render.item.model.SelectItemModel.ModelSelector;

//?}

@ExtensionMethod(ItemStackExtension.class)
@Mixin(SelectItemModel.class)
public class SelectItemModelMixin {

	//? if >=1.21.5 {
	@Shadow
	@Final
	private ModelSelector<?> selector;
	//?} else {
	/*@Shadow
	@Final
	private Object2ObjectMap<?, ItemModel> cases;
	*///?}

	@Inject(at = @At("TAIL"), method = "update")
	private void markModdedIfModelChangedWithVanillaResourcePack(CallbackInfo ci, @Local ItemModel model, @Local(argsOnly = true) ItemStack stack) {
		this.checkModel(model, stack);
	}

	@Unique
	private void checkModel(ItemModel itemModel, ItemStack stack) {
		//? if >=1.21.5 {
		ItemModel standardModel = this.selector.get(null, null);
		 //?} else {
		/*ItemModel standardModel = this.cases.get(null);
		*///?}

		if (standardModel != itemModel) {
			stack.setModdedModel(true);
		}
	}

}
//?}
