package net.lopymine.mtd.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;

public class ScreenUtils {

	private static Boolean IS_MAC = null;

	public static boolean hasShiftDown() {
		return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 344);
	}

	public static boolean hasControlDown() {
		if (IS_MAC == null) {
			IS_MAC = Util.getPlatform() == OS.OSX;
		}
		if (IS_MAC) {
			return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 347);
		} else {
			return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), 345);
		}
	}

}
