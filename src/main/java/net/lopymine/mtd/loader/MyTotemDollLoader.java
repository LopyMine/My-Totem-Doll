package net.lopymine.mtd.loader;

//? if fabric {

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.function.*;
import net.fabricmc.fabric.api.client.command.v2.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.*;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@SuppressWarnings("unused")
public class MyTotemDollLoader {

	public static boolean isModLoaded(String modid, boolean loadingPhase) {
		return FabricLoader.getInstance().isModLoaded(modid);
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

	public static void registerReloadListener(Identifier id, PreparableReloadListener listener) {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id, listener);
	}

	public static void registerClientStopping(Runnable runnable) {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> runnable.run());
	}

	public static <T extends TooltipComponent> void registerTooltipComponentFactory(Class<T> type, Function<T, ClientTooltipComponent> factory) {
		ClientTooltipComponentCallback.EVENT.register((data) -> type.isInstance(data) ? factory.apply(type.cast(data)) : null);
	}

	public static <S extends PictureInPictureRenderState> void registerPictureInPictureRenderer(Class<S> stateClass, Supplier<PictureInPictureRenderer<S>> factory) {
		PictureInPictureRendererRegistry.register((context) -> factory.get());
	}

	//~ client_fabric_commands
	public static void registerCommands(Consumer<CommandDispatcher<FabricClientCommandSource>> consumer) {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> consumer.accept(dispatcher));
	}
	//~ !client_fabric_commands

}
//?} elif neoforge {

/*import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.function.*;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.*;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.maven.artifact.versioning.*;

@SuppressWarnings("unused")
public class MyTotemDollLoader {

	public static boolean isModLoaded(String modid, boolean loadingPhase) {
		if (loadingPhase) {
			return FMLLoader.getCurrent().getLoadingModList().getModFileById(modid) != null;
		}
		return ModList.get().isLoaded(modid);
	}

	public static Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.getCurrent().isProduction();
	}

	public static int compareVersions(String first, String second) {
		return new DefaultArtifactVersion(first).compareTo(new DefaultArtifactVersion(second));
	}

	public static void registerReloadListener(Identifier id, PreparableReloadListener listener) {
		getModBus().addListener(AddClientReloadListenersEvent.class, (event) -> event.addListener(id, listener));
	}

	public static void registerClientStopping(Runnable runnable) {
		NeoForge.EVENT_BUS.addListener(ClientStoppingEvent.class, (event) -> runnable.run());
	}

	public static <T extends TooltipComponent> void registerTooltipComponentFactory(Class<T> type, Function<T, ClientTooltipComponent> factory) {
		getModBus().addListener(RegisterClientTooltipComponentFactoriesEvent.class, (event) -> event.register(type, factory));
	}

	public static <S extends PictureInPictureRenderState> void registerPictureInPictureRenderer(Class<S> stateClass, Supplier<PictureInPictureRenderer<S>> factory) {
		getModBus().addListener(RegisterPictureInPictureRenderersEvent.class, (event) -> event.register(stateClass, factory));
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
*///?}
