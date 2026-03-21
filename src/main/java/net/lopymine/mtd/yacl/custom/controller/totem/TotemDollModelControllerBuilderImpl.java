package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.*;
import net.minecraft.resources.Identifier;

public class TotemDollModelControllerBuilderImpl implements TotemDollModelControllerBuilder {

	private final Option<Identifier> option;

	public TotemDollModelControllerBuilderImpl(Option<Identifier> option) {
		this.option = option;
	}

	@Override
	public Controller<Identifier> build() {
		return new TotemDollModelController(this.option);
	}
}
