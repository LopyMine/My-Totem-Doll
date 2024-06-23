package net.lopymine.mossy.client;

import lombok.*;
import org.slf4j.*;

import net.fabricmc.api.ClientModInitializer;

import net.lopymine.mossy.Mossy;
import net.lopymine.mossy.config.MossyConfig;


public class MossyClient implements ClientModInitializer {
	public static Logger LOGGER = LoggerFactory.getLogger(Mossy.MOD_NAME + "/Client");

	@Setter
	@Getter
	private static MossyConfig config;

	@Override
	public void onInitializeClient() {
		MossyClient.config = MossyConfig.getInstance();
		LOGGER.info("{} Client Initialized", Mossy.MOD_NAME);
	}
}
