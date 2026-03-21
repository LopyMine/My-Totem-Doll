package net.lopymine.mtd.utils;

import net.minecraft.client.Minecraft;

public class LightningUtils {

	public static void disable3dLighting() {
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.ITEMS_FLAT);
	}

	public static void enable3dLighting() {
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.ITEMS_3D);
	}
}
