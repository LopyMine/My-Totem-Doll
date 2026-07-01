package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.*;
import net.minecraft.resources.ResourceLocation;

public class TotemDollModelControllerBuilderImpl implements TotemDollModelControllerBuilder {

	private final Option<ResourceLocation> option;

	public TotemDollModelControllerBuilderImpl(Option<ResourceLocation> option) {
		this.option = option;
	}

	@Override
	public Controller<ResourceLocation> build() {
		return new TotemDollModelController(this.option);
	}
}
