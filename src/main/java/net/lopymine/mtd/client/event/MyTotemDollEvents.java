package net.lopymine.mtd.client.event;

import net.lopymine.mtd.atlas.manager.*;
import net.minecraft.client.gui.tooltip.*;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.gui.tooltip.combined.*;
import net.lopymine.mtd.gui.tooltip.info.*;
import net.lopymine.mtd.gui.tooltip.preview.*;
import net.lopymine.mtd.gui.tooltip.state.LoadingStateTooltipData;
import net.lopymine.mtd.gui.tooltip.tags.*;
import net.lopymine.mtd.gui.tooltip.wrapped.*;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;

public class MyTotemDollEvents {

	public static void register() {
		registerTooltipCallbacks();
		registerLifecycleEvents();
	}

	private static void registerTooltipCallbacks() {
		TooltipComponentCallback.EVENT.register((data -> {
			if (data instanceof TagsTooltipData tooltipData) {
				return new TagsTooltipComponent(tooltipData.tags());
			}
			if (data instanceof InfoTooltipData tooltipData) {
				return new InfoTooltipComponent(tooltipData.key(), tooltipData.color());
			}
			if (data instanceof LoadingStateTooltipData tooltipData) {
				return TooltipComponent.of(MyTotemDoll.text("text.status").append(tooltipData.state().getText()).asOrderedText());
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
		}));
	}

	private static void registerLifecycleEvents() {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			MyTotemDollTaskExecutor.close();
			MyTotemDollAtlasManager.close();
			MyTotemDollAtlasSpriteManager.close();
		});
	}
}
