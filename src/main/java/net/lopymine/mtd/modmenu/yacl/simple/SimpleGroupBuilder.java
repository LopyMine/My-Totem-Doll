package net.lopymine.mtd.modmenu.yacl.simple;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.OptionGroup.Builder;
import net.minecraft.text.Text;

import net.lopymine.mtd.utils.ModMenuUtils;

public class SimpleGroupBuilder {

	private final Builder groupBuilder;

	public SimpleGroupBuilder(String groupId) {
		String groupKey = ModMenuUtils.getGroupKey(groupId);
		Text groupName = ModMenuUtils.getName(groupKey);
		Text description = ModMenuUtils.getDescription(groupKey);

		this.groupBuilder = OptionGroup.createBuilder()
				.name(groupName)
				.description(OptionDescription.of(description));
	}

	public static SimpleGroupBuilder startBuilder(String groupId) {
		return new SimpleGroupBuilder(groupId);
	}

	public SimpleGroupBuilder options(Option<?>... options) {
		for (Option<?> option : options) {
			if (option == null) {
				continue;
			}
			this.groupBuilder.option(option);
		}
		return this;
	}

	public OptionGroup build() {
		return this.groupBuilder.build();
	}
}
