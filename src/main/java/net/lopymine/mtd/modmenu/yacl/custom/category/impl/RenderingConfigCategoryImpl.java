package net.lopymine.mtd.modmenu.yacl.custom.category.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.text.Text;

import net.lopymine.mtd.modmenu.yacl.custom.category.RenderingConfigCategory;
import net.lopymine.mtd.modmenu.yacl.custom.category.tab.RenderingCategoryTab;
import net.lopymine.mtd.utils.mixin.CustomTabProvider;

public record RenderingConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups,
                                          Text tooltip) implements RenderingConfigCategory, CustomTabProvider {

	@Override
	public TabExt createTab(YACLScreen screen, ScreenRect tabArea) {
		return new RenderingCategoryTab(screen, this, tabArea);
	}
}
