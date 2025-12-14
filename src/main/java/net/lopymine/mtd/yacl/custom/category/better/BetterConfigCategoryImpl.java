package net.lopymine.mtd.yacl.custom.category.better;

import com.google.common.collect.ImmutableList;


import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.text.Text;

public record BetterConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups,
                                       Text tooltip) implements BetterConfigCategory, CustomTabProvider {

	@Override
	public TabExt createTab(YACLScreen screen, ScreenRect tabArea) {
		return new BetterCategoryTab(screen, this, tabArea);
	}
}
