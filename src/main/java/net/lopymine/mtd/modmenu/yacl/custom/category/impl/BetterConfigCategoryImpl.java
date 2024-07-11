package net.lopymine.mtd.modmenu.yacl.custom.category.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.text.Text;

import net.lopymine.mtd.modmenu.yacl.custom.category.BetterConfigCategory;
import net.lopymine.mtd.modmenu.yacl.custom.category.tab.DefaultCategoryTab;

import net.lopymine.mtd.utils.mixin.CustomTabProvider;

public record BetterConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups, Text tooltip) implements BetterConfigCategory, CustomTabProvider {
	@Override
	public TabExt createTab(YACLScreen screen, ScreenRect tabArea) {
		return new DefaultCategoryTab(screen, this, tabArea);
	}
}
