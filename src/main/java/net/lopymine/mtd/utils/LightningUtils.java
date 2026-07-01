package net.lopymine.mtd.utils;

public class LightningUtils {

	public static void disable3dLighting() {
		com.mojang.blaze3d.platform.Lighting.setupForFlatItems();
	}

	public static void enable3dLighting() {
		com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
	}
}
