package net.lopymine.mtd.modmenu.yacl.custom.category;

import dev.isxander.yacl3.api.ConfigCategory;

import net.lopymine.mtd.utils.mixin.BetterCategoryBuilder;

public interface BetterConfigCategory extends ConfigCategory {

	static Builder createBuilder() {
		return ((BetterCategoryBuilder) ConfigCategory.createBuilder()).myTotemDoll$enableBetter();
	}
}
