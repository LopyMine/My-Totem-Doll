package net.lopymine.mtd.gui.tooltip.combined;

import java.util.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record CombinedTooltipData(List<ClientTooltipComponent> list) implements TooltipComponent {

	public CombinedTooltipData(TooltipComponent... data) {
		this(Arrays.stream(data).map(ClientTooltipComponent::create).toList());
	}

}
