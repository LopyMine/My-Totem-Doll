package net.lopymine.mossy;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mossy implements ModInitializer {
	public static final String MOD_NAME = /*$ mod_name*/ "Mossy";
	public static final String MOD_ID = /*$ mod_id*/ "mossy";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	@Override
	public void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}