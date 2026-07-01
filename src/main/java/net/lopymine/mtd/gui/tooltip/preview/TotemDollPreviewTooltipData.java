package net.lopymine.mtd.gui.tooltip.preview;

import net.lopymine.mtd.doll.data.TotemDollData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record TotemDollPreviewTooltipData(TotemDollData data, ResourceLocation model) implements TooltipComponent {

}
