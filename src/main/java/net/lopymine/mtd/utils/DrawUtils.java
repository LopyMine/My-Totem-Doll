package net.lopymine.mtd.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class DrawUtils {

	public static void drawTexture(GuiGraphics context, ResourceLocation sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		context.blit(
				sprite,
				x,
				y,
				u,
				v,
				width,
				height,
				textureWidth,
				textureHeight
		);
	}

	public static void drawTooltip(GuiGraphics context, List<ClientTooltipComponent> list, int x, int y) {
		context.renderTooltipInternal(
				Minecraft.getInstance().font,
				list,
				x,
				y,
				DefaultTooltipPositioner.INSTANCE
		);
	}

	public static void drawCenteredText(GuiGraphics context, Component text, int x, int y, int width) {
		drawCenteredText(context, text, x, y, width, 0);
	}

	public static void drawCenteredText(GuiGraphics context, Component text, int x, int y, int width, int height) {
		Font textRenderer = Minecraft.getInstance().font;
		int textWidth = textRenderer.width(text);

		int centerX = x + (width / 2);
		int start = centerX - (textWidth / 2);
		int end = centerX + (textWidth / 2);

		if (start < x || end > x + width) {
			drawScrollableText(context, x, y, width, height, text);
		} else {
			context.drawString(textRenderer, text, start, y + height / 2 - (textRenderer.lineHeight / 2), -1, true);
		}
	}

	public static void drawText(GuiGraphics context, Component text, int x, int y, int width, int height) {
		Font textRenderer = Minecraft.getInstance().font;
		int textWidth = textRenderer.width(text);
		if (x + textWidth > x + width) {
			drawScrollableText(context, x, y, width, height, text);
		} else {
			context.drawString(textRenderer, text, x, y + height / 2 - (textRenderer.lineHeight / 2), -1, true);
		}
	}

	private static void drawScrollableText(GuiGraphics context, int x, int y, int width, int height, Component text) {
		Font textRenderer = Minecraft.getInstance().font;
		AbstractWidget.renderScrollingString(context, textRenderer, text, x, y, x + width, y + height, -1);
	}
}
