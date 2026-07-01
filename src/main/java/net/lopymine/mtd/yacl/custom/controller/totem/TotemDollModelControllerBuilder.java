package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.resources.ResourceLocation;

public interface TotemDollModelControllerBuilder extends ControllerBuilder<ResourceLocation> {

	static TotemDollModelControllerBuilder create(Option<ResourceLocation> option) {
		return new TotemDollModelControllerBuilderImpl(option);
	}
}
