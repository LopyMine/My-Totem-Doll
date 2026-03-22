package net.lopymine.mtd.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.cache.KnownPlayerUUIDsConfigManager;
import net.lopymine.mtd.client.command.MyTotemDollCommandManager;
import net.lopymine.mtd.client.event.MyTotemDollEvents;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.pack.MyTotemDollReloadListener;
import net.lopymine.mtd.tag.manager.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.*;

public class MyTotemDollClient implements ClientModInitializer {

	public static Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Client");

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && MyTotemDollConfig.getInstance().isModEnabled() && isProbablyTotem(stack);
	}

	@SuppressWarnings("deprecation")
	private static boolean isProbablyTotem(ItemStack stack) {
		boolean bl = stack.item != null && stack.item.value() == Items.TOTEM_OF_UNDYING;
		return bl || (MyTotemDollConfig.getInstance().isSupportOtherModsTotems() && BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().contains("totem"));
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		MyTotemDollCommandManager.register();
		MyTotemDollEvents.register();
		MyTotemDollReloadListener.register();
		KnownPlayerUUIDsConfigManager.start();
		PictureInPictureRendererRegistry.register(context -> new net.lopymine.mtd.doll.renderer.special.ItemGuiElementRenderer(context.bufferSource()));
	}
}
