package net.lopymine.mtd.gui.tooltip.combined;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.joml.Matrix4f;

import java.util.List;

public class CombinedTooltipComponent implements ClientTooltipComponent {

    private final List<ClientTooltipComponent> components;

    public CombinedTooltipComponent(List<ClientTooltipComponent> components) {
        this.components = components;
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (ClientTooltipComponent component : this.components) {
            height += component.getHeight() + 1;
        }
        return height;
    }

    @Override
    public int getWidth(Font textRenderer) {
        int width = 0;
        for (ClientTooltipComponent component : this.components) {
            int componentWidth = component.getWidth(textRenderer);
            if (componentWidth > width) width = componentWidth;
        }
        return width;
    }

	@Override
	public void renderText(Font textRenderer, int x, int y, Matrix4f matrix, BufferSource vertexConsumers) {
		int componentY = 0;
		for (ClientTooltipComponent component : this.components) {
			component.renderText(textRenderer, x, y + componentY, matrix, vertexConsumers);
			componentY += component.getHeight() + 1;
		}
	}

    @Override
    public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
        int componentY = 0;
        for (ClientTooltipComponent component : this.components) {
            component.renderImage(textRenderer, x, y + componentY,  context);
            componentY += component.getHeight() + 1;
        }
    }
}