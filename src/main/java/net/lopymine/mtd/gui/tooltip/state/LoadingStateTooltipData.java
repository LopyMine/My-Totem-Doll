package net.lopymine.mtd.gui.tooltip.state;

import net.lopymine.mtd.doll.data.LoadingState;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record LoadingStateTooltipData(LoadingState state) implements TooltipComponent {

}
