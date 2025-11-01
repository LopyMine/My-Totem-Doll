package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.gui.widget.button.ButtonListWidget;
import net.lopymine.mtd.gui.widget.list.AbstractVersionedEntryListWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/widget/EntryListWidget;enableScissor(Lnet/minecraft/client/gui/DrawContext;)V"
			),
			method = /*? if >=1.21 {*/ "renderWidget" /*?} else {*/ /*"render" *//*?}*/
	)
	private boolean disableScissorEnabling(EntryListWidget<?> instance, DrawContext context) {
		return !(((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	@WrapWithCondition(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;disableScissor()V"
			),
			method = /*? if >=1.21 {*/ "renderWidget" /*?} else {*/ /*"render" *//*?}*/
	)
	private boolean disableScissorDisabling(DrawContext instance) {
		return !(((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}

	//? if >=1.21.9 {
		@WrapOperation(
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;getHeight()I"
			),
			method = {
					"recalculateAllChildrenPositions",
					"getYOfNextEntry",
					"getContentsHeightWithPadding"
			})
	private int addOffset(EntryListWidget.Entry<?> instance, Operation<Integer> original) {
		Integer height = original.call(instance);
		if (!(((EntryListWidget<?>) (Object) this) instanceof ButtonListWidget)) {
			return height;
		}
		return height + 2;
	}

	@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;drawScrollbar(Lnet/minecraft/client/gui/DrawContext;II)V"), method = "renderWidget")
	private boolean noScrollbar(EntryListWidget<?> instance, DrawContext context, int a, int b) {
		return !(((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}
	//?}

	//? if >=1.21.4 && <=1.21.8 {
	/*@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;drawScrollbar(Lnet/minecraft/client/gui/DrawContext;)V"), method = "renderWidget")
	private boolean noScrollbar(EntryListWidget<?> instance, DrawContext context) {
		return !(((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>);
	}*///?} elif >=1.21 && <=1.21.8 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;isScrollbarVisible()Z"), method = "renderWidget")
	private boolean noScrollbar(EntryListWidget<?> instance, Operation<Boolean> original) {
		if (((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>) {
			return false;
		}
		return original.call(instance);
	}
	*///?} elif <=1.21.8 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;getMaxScroll()I"), method = "render")
	private int noScrollbar(EntryListWidget<?> instance, Operation<Integer> original) {
		if (((EntryListWidget<?>) (Object) this) instanceof AbstractVersionedEntryListWidget<?>) {
			return 0;
		}
		return original.call(instance);
	}
	*///?}

}
