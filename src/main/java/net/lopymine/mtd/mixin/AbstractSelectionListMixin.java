package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.gui.widget.list.AbstractVersionedEntryListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;enableScissor(Lnet/minecraft/client/gui/GuiGraphics;)V"
			),
			method = "renderWidget"
	)
	private boolean disableScissorEnabling(AbstractSelectionList<?> instance, GuiGraphics context) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;disableScissor()V"
			),
			method = "renderWidget"
	)
	private boolean disableScissorDisabling(GuiGraphics instance) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}


	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;scrollbarVisible()Z"), method = "renderWidget")
	private boolean noScrollbar(AbstractSelectionList<?> instance, Operation<Boolean> original) {
		if (((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>) {
			return false;
		}
		return original.call(instance);
	}

}
