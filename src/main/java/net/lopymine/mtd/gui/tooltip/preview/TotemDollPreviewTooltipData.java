package net.lopymine.mtd.gui.tooltip.preview;

import net.lopymine.mtd.doll.data.TotemDollData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TotemDollPreviewTooltipData(TotemDollData data, Identifier model) implements TooltipComponent {

}
