package net.lopymine.mtd.client.event;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.manager.*;
import net.lopymine.mtd.gui.tooltip.combined.*;
import net.lopymine.mtd.gui.tooltip.info.*;
import net.lopymine.mtd.gui.tooltip.preview.*;
import net.lopymine.mtd.gui.tooltip.state.LoadingStateTooltipData;
import net.lopymine.mtd.gui.tooltip.tags.*;
import net.lopymine.mtd.gui.tooltip.wrapped.*;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MyTotemDollEvents {

	public static void register() {
		registerTooltipCallbacks();
		registerLifecycleEvents();
	}

	private static void registerTooltipCallbacks() {
		MyTotemDollLoader.registerTooltipComponentFactory(TagsTooltipData.class, (data) -> new TagsTooltipComponent(data.tags()));
		MyTotemDollLoader.registerTooltipComponentFactory(InfoTooltipData.class, (data) -> new InfoTooltipComponent(data.key(), data.color()));
		MyTotemDollLoader.registerTooltipComponentFactory(LoadingStateTooltipData.class, (data) -> ClientTooltipComponent.create(MyTotemDoll.text("text.status").append(data.state().getText()).getVisualOrderText()));
		MyTotemDollLoader.registerTooltipComponentFactory(CombinedTooltipData.class, (data) -> new CombinedTooltipComponent(data.list()));
		MyTotemDollLoader.registerTooltipComponentFactory(TotemDollPreviewTooltipData.class, (data) -> new TotemDollPreviewTooltipComponent(data.data(), data.model()));
		MyTotemDollLoader.registerTooltipComponentFactory(WrappedTextTooltipData.class, (data) -> new WrappedTextTooltipComponent(data.text()));
	}

	private static void registerLifecycleEvents() {
		MyTotemDollLoader.registerClientStopping(() -> {
			MyTotemDollTaskExecutor.stop();
			MyTotemDollAtlasManager.close();
			MyTotemDollAtlasSpriteManager.close();
		});
	}
}
