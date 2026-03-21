package net.lopymine.mtd.yacl.custom.simple.main;

import dev.isxander.yacl3.api.*;
import net.lopymine.mtd.utils.ModMenuUtils;
import net.lopymine.mtd.yacl.custom.renderer.TotemDollPreviewRenderer;
import net.minecraft.network.chat.Component;

public class SimpleGroup {

	private final OptionGroup.Builder groupBuilder;
	private final OptionDescription.Builder description;

	public SimpleGroup(String groupId) {
		String groupKey = ModMenuUtils.getGroupKey(groupId);
		Component groupName = ModMenuUtils.getName(groupKey);
		Component description = ModMenuUtils.getDescription(groupKey);

		this.groupBuilder = OptionGroup.createBuilder().name(groupName);
		this.description  = OptionDescription.createBuilder().text(description);
	}

	public static SimpleGroup startBuilder(String groupId) {
		return new SimpleGroup(groupId);
	}

	public SimpleGroup options(Option<?>... options) {
		for (Option<?> option : options) {
			if (option == null) {
				continue;
			}
			this.groupBuilder.option(option);
		}
		return this;
	}

	public SimpleGroup withCustomDescription(TotemDollPreviewRenderer renderer) {
		this.description.customImage(renderer);
		return this;
	}

	public OptionGroup build() {
		return this.groupBuilder.description(this.description.build()).build();
	}
}
