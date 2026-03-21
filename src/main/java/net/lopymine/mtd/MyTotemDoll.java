package net.lopymine.mtd;

import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import org.slf4j.*;

public class MyTotemDoll implements ModInitializer {

	public static final String MOD_NAME = /*$ mod_name*/ "My Totem Doll";
	public static final String MOD_ID = /*$ mod_id*/ "my-totem-doll";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final String YACL_DEPEND_VERSION = /*$ yacl*/ "3.9.0+26.1-fabric";

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static Identifier getDollTextureId(String path) {
		return id("doll/textures/" + path);
	}

	public static Identifier getDollModelId(String path) {
		return id("dolls/%s.bbmodel".formatted(path));
	}

	public static MutableComponent text(String path, Object... args) {
		return Component.literal(Component.translatable(String.format("%s.%s", MOD_ID, path), args).getString().replace('&', '§'));
	}

	public static Identifier spriteId(String path) {
		return id(path);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}