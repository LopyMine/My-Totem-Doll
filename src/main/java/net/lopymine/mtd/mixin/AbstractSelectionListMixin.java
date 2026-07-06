package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.gui.widget.button.ButtonListWidget;
import net.lopymine.mtd.gui.widget.list.AbstractVersionedEntryListWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;enableScissor(Lnet/minecraft/client/gui/GuiGraphics;)V"
			),
			method = "render"
	)
	private boolean disableScissorEnabling(AbstractSelectionList<?> instance, GuiGraphics context) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;disableScissor()V"
			),
			method = "render"
	)
	private boolean disableScissorDisabling(GuiGraphics instance) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}


	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;getMaxScroll()I"), method = "render")
	private int noScrollbar(AbstractSelectionList<?> instance, Operation<Integer> original) {
		if (((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>) {
			return 0;
		}
		return original.call(instance);
	}

}
