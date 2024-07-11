package net.lopymine.mtd.modmenu.yacl.simple;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.ConfigCategory.Builder;

import net.lopymine.mtd.modmenu.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.modmenu.yacl.custom.category.RenderingConfigCategory;

public class SimpleRenderingCategoryBuilder {

	private final Builder builder;

	private SimpleRenderingCategoryBuilder() {
		this.builder = RenderingConfigCategory.createBuilder()
				.name(YACLConfigurationScreen.getRenderingCategoryTitle());
	}

	public static SimpleRenderingCategoryBuilder startBuilder() {
		return new SimpleRenderingCategoryBuilder();
	}

	public SimpleRenderingCategoryBuilder groups(OptionGroup... groups) {
		for (OptionGroup group : groups) {
			if (group == null) {
				continue;
			}
			this.builder.group(group);
		}
		return this;
	}

	public SimpleRenderingCategoryBuilder options(Option<?>... options) {
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
