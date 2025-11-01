package net.lopymine.mtd.gui.tooltip.info;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.*;

//? if >=1.21.9 {

import net.minecraft.client.font.MultilineText.Alignment;

//?}

public class InfoTooltipComponent implements TooltipComponent {

	public static final Identifier SEPARATOR = MyTotemDoll.id("textures/gui/info/separator.png");

	private final MutableText title;
	private final MultilineText text;

	public InfoTooltipComponent(String key, int color) {
		this.title = MyTotemDoll.text("%s.title".formatted(key));
		this.title.setStyle(this.title.getStyle().withColor(color));
		this.text  = MultilineText.create(MinecraftClient.getInstance().textRenderer, MyTotemDoll.text("%s.text".formatted(key)), 140);
	}

	@Override
	public int getHeight(/*? >=1.21.2 {*/TextRenderer textRenderer/*?}*/) {
		return (this.text./*? if >=1.21.9 {*/ getLineCount /*?} else {*/ /*count *//*?}*/() * 10) + 26 + 2 + 5 + 2 + 5;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return 150;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, /*? >=1.21.2 {*/int w, int h,/*?}*/ DrawContext context) {
		int width = this.getWidth(textRenderer);
		int titleWidth = textRenderer.getWidth(this.title);
		context.drawText(textRenderer, this.title, x + (((width) / 2) - (titleWidth / 2)), y + 8, -1, false);
		DrawUtils.drawTexture(context, SEPARATOR, x, y + 24, 0, 0, 150, 5, 150, 5);
		//? if >=1.21.9 {
		this.text.draw(context, Alignment.LEFT, x + 5, y + 26 + 2 + 5 + 2, 10, true, -1);
		//?} else {
		/*this.text.draw(context, x + 5, y + 26 + 2 + 5 + 2, 10, -1);
		*///?}
	}
}
