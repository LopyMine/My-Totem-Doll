package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.resources.Identifier;

public interface TotemDollModelControllerBuilder extends ControllerBuilder<Identifier> {

	static TotemDollModelControllerBuilder create(Option<Identifier> option) {
		return new TotemDollModelControllerBuilderImpl(option);
	}
}
