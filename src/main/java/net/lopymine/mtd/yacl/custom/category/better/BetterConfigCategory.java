package net.lopymine.mtd.yacl.custom.category.better;

import dev.isxander.yacl3.api.ConfigCategory;

import net.lopymine.mtd.utils.mixin.yacl.BetterYACLCategoryBuilder;

public interface BetterConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterYACLCategoryBuilder) ConfigCategory.createBuilder()).myTotemDoll$enableBetter();
	}
}
