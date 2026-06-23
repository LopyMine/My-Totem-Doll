package net.lopymine.mtd.utils;

import com.mojang.blaze3d.platform.Lighting.Entry;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class LightningUtils {

	public static void flat() {
		Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_FLAT);
	}

	public static void nonFlat() {
		Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ITEMS_3D);
	}
}
