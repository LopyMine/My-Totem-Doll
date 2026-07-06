package net.lopymine.mtd;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.*;

public class MyTotemDoll {

	public static final String MOD_NAME = /*$ mod_name*/ "My Totem Doll";
	public static final String MOD_ID = /*$ mod_id*/ "my_totem_doll";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final String YACL_DEPEND_VERSION = /*$ yacl*/ "3.6.6+1.20.1-fabric";

	public static ResourceLocation id(String path) {
		return ResourceLocation.tryBuild(MOD_ID, path);
	}

	public static ResourceLocation getDollTextureId(String path) {
		return id("doll/textures/" + path);
	}

	public static ResourceLocation getDollModelId(String path) {
		return id("dolls/%s.bbmodel".formatted(path));
	}

	public static MutableComponent text(String path, Object... args) {
		return Component.literal(Component.translatable(String.format("%s.%s", MOD_ID, path), args).getString().replace('&', '§'));
	}

	public static ResourceLocation spriteId(String path) {
		return id(String.format("textures/1.20.1/gui/sprites/%s.png", path));
	}

	public static void onInitialize() {
		LOGGER.info("{} Initialized", MOD_NAME);
	}
}