package net.lopymine.mtd.loader;

//? if fabric {

/*import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.function.*;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.loader.api.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@SuppressWarnings("unused")
public class MyTotemDollLoader {

	public static boolean isModLoaded(String modid, boolean loadingPhase) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}

	public static String getModVersion(String modid, boolean loadingPhase) {
		return FabricLoader.getInstance().getModContainer(modid).orElseThrow().getMetadata().getVersion().getFriendlyString();
	}

	public static Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static int compareVersions(String first, String second) {
		try {
			return SemanticVersion.parse(first).compareTo((Version) SemanticVersion.parse(second));
		} catch (VersionParsingException e) {
			throw new RuntimeException(e);
		}
	}

	public static void registerReloadListener(ResourceLocation id, PreparableReloadListener listener) {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener) listener);
	}

	public static void registerClientStopping(Runnable runnable) {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> runnable.run());
	}

	public static void registerAdditionalModel(ResourceLocation id) {
		ModelLoadingPlugin.register((context) -> context.addModels(id));
	}

	public static <T extends TooltipComponent> void registerTooltipComponentFactory(Class<T> type, Function<T, ClientTooltipComponent> factory) {
		TooltipComponentCallback.EVENT.register((data) -> type.isInstance(data) ? factory.apply(type.cast(data)) : null);
	}

	//~ client_fabric_commands
	public static void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> consumer.accept(dispatcher));
	}
	//~ !client_fabric_commands

}
*///?} elif neoforge {

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.function.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.*;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import org.apache.maven.artifact.versioning.*;

@SuppressWarnings("unused")
public class MyTotemDollLoader {

	public static boolean isModLoaded(String modid, boolean loadingPhase) {
		if (loadingPhase) {
			return FMLLoader.getLoadingModList().getModFileById(modid) != null;
		}
		return ModList.get().isLoaded(modid);
	}

	public static String getModVersion(String modid, boolean loadingPhase) {
		if (loadingPhase) {
			return FMLLoader.getLoadingModList().getModFileById(modid).getMods().get(0).getVersion().toString();
		}
		return ModList.get().getModContainerById(modid).orElseThrow().getModInfo().getVersion().toString();
	}

	public static Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	public static int compareVersions(String first, String second) {
		return new DefaultArtifactVersion(first).compareTo(new DefaultArtifactVersion(second));
	}

	public static void registerReloadListener(ResourceLocation id, PreparableReloadListener listener) {
		getModBus().addListener(RegisterClientReloadListenersEvent.class, (event) -> event.registerReloadListener(listener));
	}

	public static void registerClientStopping(Runnable runnable) {
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, (event) -> runnable.run());
	}

	public static void registerAdditionalModel(ResourceLocation id) {
		getModBus().addListener(ModelEvent.RegisterAdditional.class, (event) -> event.register(ModelResourceLocation.standalone(id)));
	}

	public static <T extends TooltipComponent> void registerTooltipComponentFactory(Class<T> type, Function<T, ClientTooltipComponent> factory) {
		getModBus().addListener(RegisterClientTooltipComponentFactoriesEvent.class, (event) -> event.register(type, factory));
	}

	public static void registerCommands(Consumer<CommandDispatcher<CommandSourceStack>> consumer) {
		NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, (event) -> consumer.accept(event.getDispatcher()));
	}

	private static IEventBus getModBus() {
		IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
		if (bus == null) {
			throw new IllegalArgumentException("Failed to get active mod bus");
		}
		return bus;
	}

}
//?}
