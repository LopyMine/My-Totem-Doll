package net.lopymine.mtd.gui.tooltip.combined;

import java.util.List;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class CombinedTooltipComponent implements ClientTooltipComponent {

	private final List<ClientTooltipComponent> components;

	public CombinedTooltipComponent(List<ClientTooltipComponent> components) {
		this.components = components;
	}

	@Override
	public int getHeight(Font textRenderer) {
		int height = 0;
		for (ClientTooltipComponent component : this.components) {
			height += component.getHeight(textRenderer) + 1;
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
	public void extractText(GuiGraphicsExtractor graphics, Font textRenderer, int x, int y) {
		int componentY = 0;
		for (ClientTooltipComponent component : this.components) {
			component.extractText(graphics, textRenderer, x, y + componentY);
			componentY += component.getHeight(textRenderer) + 1;
		}
	}

	@Override
	public void extractImage(Font textRenderer, int x, int y, int w, int h, GuiGraphicsExtractor context) {
		int componentY = 0;
		for (ClientTooltipComponent component : this.components) {
			component.extractImage(textRenderer, x, y + componentY, w, h, context);
			componentY += component.getHeight(textRenderer) + 1;
		}
	}
}