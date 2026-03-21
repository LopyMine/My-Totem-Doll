package net.lopymine.mtd.utils;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class DrawUtils {

	public static void drawTexture(GuiGraphicsExtractor context, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		context.blit(
				RenderPipelines.GUI_TEXTURED,
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

	public static void drawTooltip(GuiGraphicsExtractor context, List<ClientTooltipComponent> list, int x, int y) {
		context.tooltip(
				Minecraft.getInstance().font,
				list,
				x,
				y,
				DefaultTooltipPositioner.INSTANCE,
				null
		);
	}

	public static void drawCenteredText(GuiGraphicsExtractor context, Component text, int x, int y, int width) {
		drawCenteredText(context, text, x, y, width, 0);
	}

	public static void drawCenteredText(GuiGraphicsExtractor context, Component text, int x, int y, int width, int height) {
		Font textRenderer = Minecraft.getInstance().font;
		int textWidth = textRenderer.width(text);

		int centerX = x + (width / 2);
		int start = centerX - (textWidth / 2);
		int end = centerX + (textWidth / 2);

		if (start < x || end > x + width) {
			drawScrollableText(context, x, y, width, height, text);
		} else {
			context.text(textRenderer, text, start, y + height / 2 - (textRenderer.lineHeight / 2), -1, true);
		}
	}

	public static void drawText(GuiGraphicsExtractor context, Component text, int x, int y, int width, int height) {
		Font textRenderer = Minecraft.getInstance().font;
		int textWidth = textRenderer.width(text);
		if (x + textWidth > x + width) {
			drawScrollableText(context, x, y, width, height, text);
		} else {
			context.text(textRenderer, text, x, y + height / 2 - (textRenderer.lineHeight / 2), -1, true);
		}
	}

	private static void drawScrollableText(GuiGraphicsExtractor context, int x, int y, int width, int height, Component text) {
		context.textRenderer().acceptScrollingWithDefaultCenter(text, x, x + width, y, y + height);
	}
}
