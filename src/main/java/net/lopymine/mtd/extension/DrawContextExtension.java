package net.lopymine.mtd.extension;

import net.minecraft.client.gui.GuiGraphics;

public class DrawContextExtension {

	public static void push(GuiGraphics context) {
		context.pose().pushMatrix();
	}

	public static void pop(GuiGraphics context) {
		context.pose().popMatrix();
	}

	public static void translate(GuiGraphics context, float x, float y, float z) {
		context.pose().translate(x, y);
	}

	public static void scale(GuiGraphics context, float x, float y, float z) {
		context.pose().scale(x, y);
	}

	public static void rotateZ(GuiGraphics context, float angle) {
		context.pose().rotate(angle * ((float) Math.PI / 180F));
	}

	public static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
		context.fill(x, y, x + width, y + 1, color);
		context.fill(x, y + height - 1, x + width, y + height, color);
		context.fill(x, y + 1, x + 1, y + height - 1, color);
		context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
	}

}
