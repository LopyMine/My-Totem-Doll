package net.lopymine.mtd.client.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.manager.*;
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
		TooltipComponentCallback.EVENT.register((data -> {
			if (data instanceof TagsTooltipData(String tags)) {
				return new TagsTooltipComponent(tags);
			}
			if (data instanceof InfoTooltipData(String key, int color)) {
				return new InfoTooltipComponent(key, color);
			}
			if (data instanceof LoadingStateTooltipData(net.lopymine.mtd.doll.data.LoadingState state)) {
				return ClientTooltipComponent.create(MyTotemDoll.text("text.status").append(state.getText()).getVisualOrderText());
			}
			if (data instanceof CombinedTooltipData(java.util.List<ClientTooltipComponent> list)) {
				return new CombinedTooltipComponent(list);
			}
			if (data instanceof TotemDollPreviewTooltipData(
					net.lopymine.mtd.doll.data.TotemDollData data1, net.minecraft.resources.Identifier model
			)) {
				return new TotemDollPreviewTooltipComponent(data1, model);
			}
			if (data instanceof WrappedTextTooltipData(net.minecraft.network.chat.Component text)) {
				return new WrappedTextTooltipComponent(text);
			}
			return null;
		}));
	}

	private static void registerLifecycleEvents() {
		ClientLifecycleEvents.CLIENT_STOPPING.register((client) -> {
			MyTotemDollTaskExecutor.stop();
			MyTotemDollAtlasManager.close();
			MyTotemDollAtlasSpriteManager.close();
		});
	}
}
