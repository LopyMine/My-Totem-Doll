package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record TotemDollModelController(Option<ResourceLocation> option) implements Controller<ResourceLocation> {

	@Override
	public Component formatValue() {
		ResourceLocation identifier = this.option.pendingValue();
		return MyTotemDoll.text("text.nice_id.quoted", identifier.getNamespace(), identifier.getPath());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new TotemDollModelControllerElement(this, screen, widgetDimension);
	}
}
