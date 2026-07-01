package net.lopymine.mtd.yacl.custom.category.better;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public record BetterConfigCategoryImpl(Component name, ImmutableList<OptionGroup> groups,
                                       Component tooltip) implements BetterConfigCategory, CustomTabProvider {

	@Override
	public TabExt createTab(YACLScreen screen, ScreenRectangle tabArea) {
		return new BetterCategoryTab(screen, this, tabArea);
	}
}
