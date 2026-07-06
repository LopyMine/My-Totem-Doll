package net.lopymine.mtd.gui.tooltip.preview;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import net.minecraft.resources.ResourceLocation;

import net.lopymine.mtd.doll.data.TotemDollData;

public record TotemDollPreviewTooltipData(TotemDollData data, ResourceLocation model) implements TooltipComponent {

}
