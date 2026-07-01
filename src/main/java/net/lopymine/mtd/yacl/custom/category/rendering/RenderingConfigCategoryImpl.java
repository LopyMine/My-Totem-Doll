package net.lopymine.mtd.yacl.custom.category.rendering;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

public record RenderingConfigCategoryImpl(Component name, ImmutableList<OptionGroup> groups,
                                          Component tooltip) implements RenderingConfigCategory, CustomTabProvider {

	@Override
	public TabExt createTab(YACLScreen screen, ScreenRectangle tabArea) {
		return new RenderingCategoryTab(screen, this, tabArea);
	}
}
