package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public record TotemDollModelController(Option<Identifier> option) implements Controller<Identifier> {

	@Override
	public Component formatValue() {
		Identifier identifier = this.option.pendingValue();
		return MyTotemDoll.text("text.nice_id.quoted", identifier.getNamespace(), identifier.getPath());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new TotemDollModelControllerElement(this, screen, widgetDimension);
	}
}
