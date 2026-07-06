package net.lopymine.mtd.gui.tooltip.wrapped;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.network.chat.Component;

public record WrappedTextTooltipData(Component text) implements TooltipComponent {

}
