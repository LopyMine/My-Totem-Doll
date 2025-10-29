package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import lombok.experimental.ExtensionMethod;
import net.minecraft.item.*;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ItemStackExtension;

@Mixin(AnvilScreenHandler.class)
@ExtensionMethod(ItemStackExtension.class)
public class AnvilScreenHandlerMixin {

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"),
			method = "updateResult"
	)
	private Text swapItemName(ItemStack stack, Operation<Text> original) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return original.call(stack);
		}
		Text customName = stack.getRealCustomName();
		if (customName == null) {
			return original.call(stack);
		}
		return customName;
	}

}
