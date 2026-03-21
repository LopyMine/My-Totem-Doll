package net.lopymine.mtd.utils.tooltip;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface TooltipRequest {

	void renderRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta);

}
