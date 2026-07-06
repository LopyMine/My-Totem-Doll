package net.lopymine.mtd.utils;

import net.minecraft.resources.ResourceLocation;

public record ButtonTextures(ResourceLocation enabled, ResourceLocation disabled, ResourceLocation enabledHovered, ResourceLocation disabledHovered) {

	public ResourceLocation get(boolean enabled, boolean hovered) {
		return enabled ? (hovered ? this.enabledHovered : this.enabled) : (hovered ? this.disabledHovered : this.disabled);
	}

}
