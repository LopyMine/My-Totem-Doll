package net.lopymine.mtd.client.event;

import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.manager.*;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.gui.tooltip.combined.*;
import net.lopymine.mtd.gui.tooltip.info.*;
import net.lopymine.mtd.gui.tooltip.preview.*;
import net.lopymine.mtd.gui.tooltip.state.LoadingStateTooltipData;
import net.lopymine.mtd.gui.tooltip.tags.*;
import net.lopymine.mtd.gui.tooltip.wrapped.*;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class MyTotemDollEvents {

	public static void register() {
		registerTooltipCallbacks();
		registerLifecycleEvents();
	}

	private static void registerTooltipCallbacks() {
		ClientTooltipComponentCallback.EVENT.register((data) -> {
			if (data instanceof TagsTooltipData tooltipData) {
				return new TagsTooltipComponent(tooltipData.tags());
			}
			if (data instanceof InfoTooltipData tooltipData) {
				return new InfoTooltipComponent(tooltipData.key(), tooltipData.color());
			}
			if (data instanceof LoadingStateTooltipData tooltipData) {
				return ClientTooltipComponent.create(MyTotemDoll.text("text.status").append(tooltipData.state().getText()).getVisualOrderText());
			}
			if (data instanceof CombinedTooltipData tooltipData) {
				return new CombinedTooltipComponent(tooltipData.list());
			}
			if (data instanceof TotemDollPreviewTooltipData tooltipData) {
				return new TotemDollPreviewTooltipComponent(tooltipData.data(), tooltipData.model());
			}
			if (data instanceof WrappedTextTooltipData tooltipData) {
				return new WrappedTextTooltipComponent(tooltipData.text());
			}
			return null;
		});
	}

	private static void registerLifecycleEvents() {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			MyTotemDollTaskExecutor.stop();
			MyTotemDollAtlasManager.close();
			MyTotemDollAtlasSpriteManager.close();
		});
	}
}
