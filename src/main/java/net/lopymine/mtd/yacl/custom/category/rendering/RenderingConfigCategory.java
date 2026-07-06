package net.lopymine.mtd.yacl.custom.category.rendering;

import dev.isxander.yacl3.api.ConfigCategory;

import net.lopymine.mtd.utils.mixin.yacl.BetterYACLCategoryBuilder;

public interface RenderingConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterYACLCategoryBuilder) ConfigCategory.createBuilder()).myTotemDoll$enableRendering();
	}
}
