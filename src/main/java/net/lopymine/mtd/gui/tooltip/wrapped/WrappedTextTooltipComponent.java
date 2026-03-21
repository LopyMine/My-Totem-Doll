package net.lopymine.mtd.gui.tooltip.wrapped;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class WrappedTextTooltipComponent implements ClientTooltipComponent {

	private final List<FormattedCharSequence> texts;

	public WrappedTextTooltipComponent(Component text) {
		this.texts = Minecraft.getInstance().font.split(text, 100000);
	}

	public int getWidth(Font textRenderer) {
		int max = 0;
		for (FormattedCharSequence text : this.texts) {
			int width = textRenderer.width(text);
			if (width > max) {
				max = width;
			}
		}
		return max;
	}

	@Override
	public int getHeight(Font textRenderer) {
		return this.texts.size() * 10;
	}

	@Override
	public void extractImage(Font textRenderer, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		int offset = 0;
		for (FormattedCharSequence text : this.texts) {
			graphics.text(textRenderer, text, x, y + offset, -1, true);
			offset += 10;
		}
	}
}
