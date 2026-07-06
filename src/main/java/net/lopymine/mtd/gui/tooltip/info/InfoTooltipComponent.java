package net.lopymine.mtd.gui.tooltip.info;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.*;



public class InfoTooltipComponent implements ClientTooltipComponent {

	public static final ResourceLocation SEPARATOR = MyTotemDoll.id("textures/gui/info/separator.png");

	private final MutableComponent title;
	private final MultiLineLabel text;

	public InfoTooltipComponent(String key, int color) {
		this.title = MyTotemDoll.text("%s.title".formatted(key));
		this.title.setStyle(this.title.getStyle().withColor(color));
		this.text  = MultiLineLabel.create(Minecraft.getInstance().font, MyTotemDoll.text("%s.text".formatted(key)), 140);
	}

	@Override
	public int getHeight() {
		return (this.text.getLineCount() * 10) + 26 + 2 + 5 + 2 + 5;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return 150;
	}

	@Override
	public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
		int width = this.getWidth(textRenderer);
		int titleWidth = textRenderer.width(this.title);
		context.drawString(textRenderer, this.title, x + (((width) / 2) - (titleWidth / 2)), y + 8, -1, false);
		DrawUtils.drawTexture(context, SEPARATOR, x, y + 24, 0, 0, 150, 5, 150, 5);
		this.text.renderLeftAlignedNoShadow(context, x + 5, y + 26 + 2 + 5 + 2, 10, -1);
	}
}
