package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import java.util.function.Supplier;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.mixin.ItemRenderStateWithStack;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {

	@Inject(at = @At("HEAD"), method = "appendItemLayers")
	private void captureEntityForDoll(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level world, ItemOwner context, int seed, CallbackInfo ci) {
		this.captureEntity(stack, context == null ? null : context.asLivingEntity(), renderState);
	}

	@WrapOperation(
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
			),
			method = "appendItemLayers"
	)
	private Object swapItemModel(ItemStack stack, DataComponentType<?> componentType, Operation<?> original) {
		return this.changeModel(stack, () -> original.call(stack, componentType));
	}

	@Unique
	private Object changeModel(ItemStack stack, Supplier<Object> supplier) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return supplier.get();
		}

		if (TotemDollPlugin.work(stack)) {
			stack.setModdedModel(true);
			return TotemDollPlugin.ID;
		}

		stack.setModdedModel(false);
		return supplier.get();
	}

	@Unique
	private void captureEntity(ItemStack stack, @Nullable LivingEntity entity, ItemStackRenderState renderState) {
		stack.setPlayerEntity(null);
		if (entity instanceof AbstractClientPlayer player) {
			stack.setPlayerEntity(player);
		}
		if (renderState instanceof ItemRenderStateWithStack itemRenderStateWithStack) {
			itemRenderStateWithStack.myTotemDoll$setStack(stack);
		}
	}

}
