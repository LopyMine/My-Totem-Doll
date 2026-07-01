package net.lopymine.mtd.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import net.minecraft.client.Minecraft;

public class ScreenUtils {

	private static Boolean IS_MAC = null;

	public static boolean hasShiftDown() {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
	}

	public static boolean hasControlDown() {
		if (IS_MAC == null) {
			IS_MAC = Util.getPlatform() == OS.OSX;
		}
		if (IS_MAC) {
			return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
		} else {
			return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
		}
	}

}
