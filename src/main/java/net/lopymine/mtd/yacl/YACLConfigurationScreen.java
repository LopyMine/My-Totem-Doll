package net.lopymine.mtd.yacl;

import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.utils.ModMenuUtils;
import net.lopymine.mtd.yacl.category.*;
import net.lopymine.mtd.yacl.custom.screen.*;
import net.lopymine.mtd.yacl.custom.simple.SimpleYACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YACLConfigurationScreen {

	public static Screen createScreen(Screen parent) {
		MyTotemDollConfig defConfig = MyTotemDollConfig.getNewInstance();
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();

		return SimpleYACLScreen.startBuilder(parent, config::save)
				.categories(GeneralCategory.get(defConfig, config))
				.categories(RenderingCategory.get(defConfig, config))
				.categories(StandardDollCategory.get(defConfig, config))
				.build();
	}

	public static boolean notOpen(Screen currentScreen) {
		return !(currentScreen instanceof MyTotemDollYACLScreen || currentScreen instanceof TotemDollModelSelectionScreen);
	}

	public static Component getRenderingCategoryTitle() {
		return ModMenuUtils.getName(ModMenuUtils.getCategoryKey("rendering"));
	}
}


