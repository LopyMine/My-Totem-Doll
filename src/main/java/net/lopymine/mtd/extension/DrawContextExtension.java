package net.lopymine.mtd.extension;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;

public class DrawContextExtension {

	public static void push(GuiGraphics context) {
		context.pose().pushPose();
	}

	public static void pop(GuiGraphics context) {
		context.pose().popPose();
	}

	public static void translate(GuiGraphics context, float x, float y, float z) {
		context.pose().translate(x, y, z);
	}

	public static void scale(GuiGraphics context, float x, float y, float z) {
		context.pose().scale(x, y, z);
	}

	public static void rotateZ(GuiGraphics context, float angle) {
		context.pose().mulPose(Axis.ZP.rotationDegrees(angle));
	}

	public static void drawBorder(GuiGraphics context, int x, int y, int width, int height, int color) {
		context.renderOutline(x, y, width, height, color);
	}

}
