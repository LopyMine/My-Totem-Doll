package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.gui.widget.button.ButtonListWidget;
import net.lopymine.mtd.gui.widget.list.AbstractVersionedEntryListWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;enableScissor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"
			),
			method = "extractWidgetRenderState"
	)
	private boolean disableScissorEnabling(AbstractSelectionList<?> instance, GuiGraphicsExtractor context) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;disableScissor()V"
			),
			method = "extractWidgetRenderState"
	)
	private boolean disableScissorDisabling(GuiGraphicsExtractor instance) {
		return !(((AbstractSelectionList<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	@WrapOperation(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;getHeight()I"
			),
			method = {
					"repositionEntries",
					"getNextY",
					"contentHeight"
			})
	private int addOffset(AbstractSelectionList.Entry<?> instance, Operation<Integer> original) {
		Integer height = original.call(instance);
		if (!(((AbstractSelectionList<?>) (Object) this) instanceof ButtonListWidget)) {
			return height;
		}
		return height + 2;
	}

}
