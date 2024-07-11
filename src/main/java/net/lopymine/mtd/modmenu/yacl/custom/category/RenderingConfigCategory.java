package net.lopymine.mtd.modmenu.yacl.custom.category;

import dev.isxander.yacl3.api.ConfigCategory;

import net.lopymine.mtd.utils.mixin.BetterCategoryBuilder;

public interface RenderingConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterCategoryBuilder) ConfigCategory.createBuilder()).myTotemDoll$enableRendering();
	}
}
