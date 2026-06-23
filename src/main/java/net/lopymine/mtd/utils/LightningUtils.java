package net.lopymine.mtd.utils;

import com.mojang.blaze3d.platform.Lighting.Entry;
import net.minecraft.client.Minecraft;

public class LightningUtils {

	public static void disable3dLighting() {
		Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_FLAT);
	}

	public static void enable3dLighting() {
		Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
	}
}
