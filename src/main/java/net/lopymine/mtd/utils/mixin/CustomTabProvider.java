package net.lopymine.mtd.utils.mixin;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.gui.ScreenRect;

public interface CustomTabProvider {

	TabExt createTab(YACLScreen screen, ScreenRect tabArea);
}
