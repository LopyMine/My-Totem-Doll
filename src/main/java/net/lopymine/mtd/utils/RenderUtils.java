package net.lopymine.mtd.utils;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderUtils {

	public static void enableBlend() {
		RenderSystem.enableBlend();
	}

	public static void enableDepthTest() {
		RenderSystem.enableDepthTest();
	}

	public static void disableBlend() {
		RenderSystem.disableBlend();
	}

	public static void disableDepthTest() {
		RenderSystem.disableDepthTest();
	}

}
