package net.lopymine.mtd.yacl.custom.category.rendering;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.text.Text;

public record RenderingConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups,
                                          Text tooltip) implements RenderingConfigCategory, CustomTabProvider {

	@Override
	public TabExt createTab(YACLScreen screen, ScreenRect tabArea) {
		return new RenderingCategoryTab(screen, this, tabArea);
	}
}
