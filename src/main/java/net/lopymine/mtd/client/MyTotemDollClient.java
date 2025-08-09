package net.lopymine.mtd.client;

import lombok.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
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

	@Setter
	@Getter
	private static MyTotemDollConfig config;

	@Override
	public void onInitializeClient() {
		MyTotemDollClient.config = MyTotemDollConfig.getInstance();
		LOGGER.info("{} Client Initialized", MyTotemDoll.MOD_NAME);
		TagsManager.register();
		TagsSkinProviders.register();
		MyTotemDollCommandManager.register();
		MyTotemDollEvents.register();
		MyTotemDollReloadListener.register();
		TotemDollPlugin.register();
		TotemDollModelFinder.registerBuiltinModels();
		//? if >=1.21.6 {
		net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry.register(
				context -> new net.lopymine.mtd.doll.renderer.special.ItemGuiElementRenderer(context.vertexConsumers()));
		//?}
	}

	public static boolean canProcess(@Nullable ItemStack stack) {
		return stack != null && MyTotemDollClient.getConfig().isModEnabled() && isProbablyTotem(stack);
	}

	private static boolean isProbablyTotem(ItemStack stack) {
		return stack.isOf(Items.TOTEM_OF_UNDYING) || (MyTotemDollClient.getConfig().isSupportOtherModsTotems() && Registries.ITEM.getId(stack.getItem()).getPath().contains("totem"));
	}
}
