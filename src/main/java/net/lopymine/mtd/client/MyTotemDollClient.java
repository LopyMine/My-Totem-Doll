package net.lopymine.mtd.client;

import net.lopymine.mtd.cache.KnownPlayerUUIDsConfigManager;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;
import org.slf4j.*;
import net.fabricmc.api.ClientModInitializer;

import net.lopymine.mtd.*;
import net.lopymine.mtd.client.command.MyTotemDollCommandManager;
import net.lopymine.mtd.client.event.MyTotemDollEvents;


import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.pack.*;
import net.lopymine.mtd.tag.manager.*;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;

import org.jetbrains.annotations.Nullable;

public class MyTotemDollClient implements ClientModInitializer {

	public static Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Client");

	@Override
	public void onInitializeClient() {
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		MyTotemDollCommandManager.register();
		MyTotemDollEvents.register();
		MyTotemDollReloadListener.register();
		TotemDollPlugin.register();
		KnownPlayerUUIDsConfigManager.start();
		//? if >=1.21.6 {
		net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry.register(
				context -> new net.lopymine.mtd.doll.renderer.special.ItemGuiElementRenderer(context.vertexConsumers()));
		//?}
	}

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && MyTotemDollConfig.getInstance().isModEnabled() && isProbablyTotem(stack);
	}

	@SuppressWarnings("deprecation")
	private static boolean isProbablyTotem(ItemStack stack) {
		return stack.item == Items.TOTEM_OF_UNDYING || (MyTotemDollConfig.getInstance().isSupportOtherModsTotems() && Registries.ITEM.getId(stack.getItem()).getPath().contains("totem"));
	}
}
