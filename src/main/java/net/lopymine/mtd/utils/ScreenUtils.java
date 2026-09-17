package net.lopymine.mtd.utils;

import net.minecraft.client.Minecraft;

public class ScreenUtils {

	public static boolean hasShiftDown() {
		return Minecraft.getInstance().hasShiftDown();
	}

	public static boolean hasControlDown() {
		return Minecraft.getInstance().hasControlDown();
	}

}
