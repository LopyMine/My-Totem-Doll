package net.lopymine.mtd.yacl.custom.simple.main;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.ConfigCategory.Builder;
import net.lopymine.mtd.utils.ModMenuUtils;
import net.lopymine.mtd.yacl.custom.category.better.BetterConfigCategory;
import net.minecraft.network.chat.Component;

public class SimpleCategory {

	private final Builder builder;

	private SimpleCategory(String categoryId) {
		String categoryKey = ModMenuUtils.getCategoryKey(categoryId);
		Component categoryName = ModMenuUtils.getName(categoryKey);
		this.builder = BetterConfigCategory.createBuilder().name(categoryName);
	}

	public static SimpleCategory startBuilder(String categoryId) {
		return new SimpleCategory(categoryId);
	}

	public SimpleCategory groups(OptionGroup... groups) {
		for (OptionGroup group : groups) {
			if (group == null) {
				continue;
			}
			this.builder.group(group);
		}
		return this;
	}

	public SimpleCategory options(Option<?>... options) {
		for (Option<?> option : options) {
			if (option == null) {
				continue;
			}
			this.builder.option(option);
		}
		return this;
	}

	public ConfigCategory build() {
		return this.builder.build();
	}
}
