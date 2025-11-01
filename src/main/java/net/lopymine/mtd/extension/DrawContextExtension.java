package net.lopymine.mtd.extension;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.RotationAxis;

public class DrawContextExtension {

	public static void push(DrawContext context) {
		//? if >=1.21.6 {
		context.getMatrices().pushMatrix();
		//?} else {
		/*context.getMatrices().push();
		*///?}
	}

	public static void pop(DrawContext context) {
		//? if >=1.21.6 {
		context.getMatrices().popMatrix();
		//?} else {
		/*context.getMatrices().pop();
		*///?}
	}

	public static void translate(DrawContext context, float x, float y, float z) {
		//? if >=1.21.6 {
		context.getMatrices().translate(x, y);
		//?} else {
		/*context.getMatrices().translate(x, y, z);
		 *///?}
	}

	public static void scale(DrawContext context, float x, float y, float z) {
		//? if >=1.21.6 {
		context.getMatrices().scale(x, y);
		//?} else {
		/*context.getMatrices().scale(x, y, z);
		 *///?}
	}

	public static void rotateZ(DrawContext context, float angle) {
		//? if >=1.21.6 {
		context.getMatrices().rotate(angle * ((float) Math.PI / 180F));
		//?} else {
		/*context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle));
		 *///?}
	}

	public static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
		//? if >=1.21.9 {
		context.fill(x, y, x + width, y + 1, color);
		context.fill(x, y + height - 1, x + width, y + height, color);
		context.fill(x, y + 1, x + 1, y + height - 1, color);
		context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
		//?} else {
		/*context.drawBorder(x, y, width, height, color);
		 *///?}
	}

}
