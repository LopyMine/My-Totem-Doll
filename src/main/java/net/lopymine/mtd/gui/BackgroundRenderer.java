package net.lopymine.mtd.gui;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.DrawContextExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import net.lopymine.mtd.utils.*;
import net.lopymine.mtd.yacl.custom.TransparencySprites;

@ExtensionMethod(DrawContextExtension.class)
public class BackgroundRenderer {

	public static void drawTransparencyWidgetBackground(GuiGraphics context, int x, int y, int width, int height, boolean enabled, boolean hovered) {
		ResourceLocation menuBackgroundTexture = enabled ? TransparencySprites.getMenuListBackgroundTexture() : TransparencySprites.DARKER_MENU_BACKGROUND_TEXTURE;
		ResourceLocation menuSeparatorTexture = TransparencySprites.getMenuSeparatorTexture();
		drawTransparencyBackground(context, x, y, width, height, menuBackgroundTexture, menuSeparatorTexture, true, true, true, true);
		if (hovered) {
			context.renderOutline(x, y, width, height, -1);
		}
	}

	public static void drawTransparencyWidgetBackground(GuiGraphics context, int x, int y, int width, int height, boolean enabled, int borderColor) {
		ResourceLocation menuBackgroundTexture = enabled ? TransparencySprites.getMenuListBackgroundTexture() : TransparencySprites.DARKER_MENU_BACKGROUND_TEXTURE;
		ResourceLocation menuSeparatorTexture = TransparencySprites.getMenuSeparatorTexture();
		drawTransparencyBackground(context, x, y, width, height, menuBackgroundTexture, menuSeparatorTexture, true, true, true, true);
		context.renderOutline(x, y, width, height, borderColor);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, boolean list) {
		drawTransparencyBackground(context, x, y, width, height, list, true, true, true, true);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, boolean list, boolean up) {
		drawTransparencyBackground(context, x, y, width, height, list, up, true, true, true);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, boolean list, boolean up, boolean bottom) {
		drawTransparencyBackground(context, x, y, width, height, list, up, bottom, true, true);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, boolean list, boolean up, boolean bottom, boolean right) {
		drawTransparencyBackground(context, x, y, width, height, list, up, bottom, right, true);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, boolean list, boolean up, boolean bottom, boolean right, boolean left) {
		ResourceLocation menuBackgroundTexture = list ? TransparencySprites.getMenuListBackgroundTexture() : TransparencySprites.getMenuBackgroundTexture();
		ResourceLocation menuSeparatorTexture = TransparencySprites.getMenuSeparatorTexture();
		drawTransparencyBackground(context, x, y, width, height, menuBackgroundTexture, menuSeparatorTexture, up, bottom, right, left);
	}

	public static void drawTransparencyBackground(GuiGraphics context, int x, int y, int width, int height, ResourceLocation backgroundTexture, ResourceLocation separatorTexture, boolean up, boolean bottom, boolean right, boolean left) {
		RenderUtils.enableBlend();

		// BACKGROUND
		DrawUtils.drawTexture(context, backgroundTexture, x + 2, y + 2, 0, 0, width - 4, height - 4, 32, 32);

		// UP
		if (up) {
			DrawUtils.drawTexture(context, separatorTexture, x + 2, y,0, 0, width - 4, 2, 32, 2);
		}

		// BOTTOM
		if (bottom) {
			context.push();
			context.translate(x + width - 2, y + height, 0);
			context.rotateZ(180);
			DrawUtils.drawTexture(context, separatorTexture, 0, 0, 0, 0, width - 4, 2, 32, 2);
			context.pop();
		}

		// RIGHT
		if (right) {
			context.push();
			context.translate(x + width, y + 1, 0);
			context.rotateZ(90);
			DrawUtils.drawTexture(context, separatorTexture, 0, 0, 0f, 0f, height - 2, 2, 32, 2);
			context.pop();
		}

		// LEFT
		if (left) {
			context.push();
			context.translate(x, y + height - 1, 0);
			context.rotateZ(-90);
			DrawUtils.drawTexture(context, separatorTexture, 0, 0, 0f, 0f, height - 2, 2, 32, 2);
			context.pop();
		}

		// UP-LEFT CORNER
		if (up && left) {
			DrawUtils.drawTexture(context, separatorTexture, x, y, 0, 0, 2, 1, 1, 0);
		}

		// UP-RIGHT CORNER
		if (up && right) {
			DrawUtils.drawTexture(context, separatorTexture, x + width - 2, y, 0, 0, 2, 1, 1, 0);
		}

		// BOTTOM-LEFT CORNER
		if (bottom && left) {
			DrawUtils.drawTexture(context, separatorTexture, x, y + height - 1, 0, 0, 2, 1, 1, 0);
		}

		// BOTTOM-RIGHT CORNER
		if (bottom && right) {
			DrawUtils.drawTexture(context, separatorTexture, x + width - 2, y + height - 1, 0, 0, 2, 1, 1, 0);
		}

		RenderUtils.disableBlend();
	}
}
