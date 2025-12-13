package net.lopymine.mtd;

import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.slf4j.*;

import net.fabricmc.api.ModInitializer;

public class MyTotemDoll implements ModInitializer {

	public static final String MOD_NAME = /*$ mod_name*/ "My Totem Doll";
	public static final String MOD_ID = /*$ mod_id*/ "my-totem-doll";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final String YACL_DEPEND_VERSION = /*$ yacl*/ "3.8.1+1.21.11-fabric";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static Identifier getDollTextureId(String path) {
		return id("doll/textures/" + path);
	}

	public static Identifier getDollModelId(String path) {
		return id("dolls/%s.bbmodel".formatted(path));
	}

	public static MutableText text(String path, Object... args) {
		return Text.literal(Text.translatable(String.format("%s.%s", MOD_ID, path), args).getString().replace('&', '§'));
	}

	public static Identifier spriteId(String path) {
		//? if >=1.20.2 {
		return id(path);
		//?} else {
		/*return id(String.format("textures/1.20.1/gui/sprites/%s.png", path));
		 *///?}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}