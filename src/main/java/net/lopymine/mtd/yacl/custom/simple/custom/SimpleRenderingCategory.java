package net.lopymine.mtd.yacl.custom.simple.custom;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.ConfigCategory.Builder;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.category.rendering.RenderingConfigCategory;

public class SimpleRenderingCategory {

	private final Builder builder;

	private SimpleRenderingCategory() {
		this.builder = RenderingConfigCategory.createBuilder()
				.name(YACLConfigurationScreen.getRenderingCategoryTitle());
	}

	public static SimpleRenderingCategory startBuilder() {
		return new SimpleRenderingCategory();
	}

	public SimpleRenderingCategory groups(OptionGroup... groups) {
		for (OptionGroup group : groups) {
			if (group == null) {
				continue;
			}
			this.builder.group(group);
		}
		return this;
	}

	public SimpleRenderingCategory options(Option<?>... options) {
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
