package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import lombok.experimental.ExtensionMethod;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ItemStackExtension;

@Mixin(AnvilMenu.class)
@ExtensionMethod(ItemStackExtension.class)
public class AnvilMenuMixin {

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"),
			method = "createResult"
	)
	private Component swapItemName(ItemStack stack, Operation<Component> original) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return original.call(stack);
		}
		Component customName = stack.getRealCustomName();
		if (customName == null) {
			return original.call(stack);
		}
		return customName;
	}

}
