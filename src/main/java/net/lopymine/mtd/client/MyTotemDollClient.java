package net.lopymine.mtd.client;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.cache.KnownPlayerUUIDsConfigManager;
import net.lopymine.mtd.client.command.MyTotemDollCommandManager;
import net.lopymine.mtd.client.event.MyTotemDollEvents;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import net.lopymine.mtd.pack.MyTotemDollReloadListener;
import net.lopymine.mtd.tag.manager.*;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

public class MyTotemDollClient {

	public static Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Client");

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && MyTotemDollConfig.getInstance().isModEnabled() && isProbablyTotem(stack);
	}

	@SuppressWarnings("deprecation")
	private static boolean isProbablyTotem(ItemStack stack) {
		return stack.item == Items.TOTEM_OF_UNDYING || (MyTotemDollConfig.getInstance().isSupportOtherModsTotems() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("totem"));
	}

	public static void onInitializeClient() {
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		MyTotemDollLoader.registerCommands(MyTotemDollCommandManager::register);
		MyTotemDollEvents.register();
		MyTotemDollReloadListener.register();
		TotemDollPlugin.register();
		KnownPlayerUUIDsConfigManager.start();
	}
}
