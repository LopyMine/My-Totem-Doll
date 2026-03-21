package net.lopymine.mtd.gui.tooltip.info;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class InfoTooltipComponent implements ClientTooltipComponent {

	public static final Identifier SEPARATOR = MyTotemDoll.id("textures/gui/info/separator.png");

	private final MutableComponent title;
	private final MultiLineLabel text;

	public InfoTooltipComponent(String key, int color) {
		this.title = MyTotemDoll.text("%s.title".formatted(key));
		this.title.setStyle(this.title.getStyle().withColor(color));
		this.text = MultiLineLabel.create(Minecraft.getInstance().font, MyTotemDoll.text("%s.text".formatted(key)), 140);
	}

	@Override
	public int getHeight(Font textRenderer) {
		return (this.text.getLineCount() * 10) + 26 + 2 + 5 + 2 + 5;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return 150;
	}

	@Override
	public void extractImage(Font textRenderer, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		int width = this.getWidth(textRenderer);
		int titleWidth = textRenderer.width(this.title);
		graphics.text(textRenderer, this.title, x + (((width) / 2) - (titleWidth / 2)), y + 8, -1, false);
		DrawUtils.drawTexture(graphics, SEPARATOR, x, y + 24, 0, 0, 150, 5, 150, 5);
		this.text.visitLines(TextAlignment.LEFT, x + 5, y + 26 + 2 + 5 + 2, 10, graphics.textRenderer());
	}
}
