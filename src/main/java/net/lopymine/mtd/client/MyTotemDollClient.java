package net.lopymine.mtd.client;

import lombok.*;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.slf4j.*;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.command.MyTotemDollCommandManager;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.layer.DollLayers;
import net.lopymine.mtd.doll.tag.DollTagManager;

public class MyTotemDollClient implements ClientModInitializer {

	public static Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Client");

	@Setter
	@Getter
	private static MyTotemDollConfig config;

	@Override
	public void onInitializeClient() {
		MyTotemDollClient.config = MyTotemDollConfig.getInstance();
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		DollLayers.register();
		DollTagManager.register();
		MyTotemDollCommandManager.register();
	}
}
