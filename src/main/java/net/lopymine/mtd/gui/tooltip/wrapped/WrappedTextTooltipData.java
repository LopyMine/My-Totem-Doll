package net.lopymine.mtd.gui.tooltip.wrapped;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record WrappedTextTooltipData(Component text) implements TooltipComponent {

}
