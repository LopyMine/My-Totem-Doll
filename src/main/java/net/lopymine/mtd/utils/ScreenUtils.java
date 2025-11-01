package net.lopymine.mtd.utils;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class ScreenUtils {

	//? if >=1.21.9 {
	public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 344);
	}
	//?} else {
	/*public static boolean hasShiftDown() {
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
	}
	*///?}

	//? if >=1.21.9 {
	public static boolean hasControlDown() {
		if (MyTotemDollClient.IS_MAC) {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 347);
		} else {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), 345);
		}
	}
	//?} else {
	/*public static boolean hasControlDown() {
		if (MyTotemDollClient.IS_MAC) {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 347);
		} else {
			return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 345);
		}
	}
	*///?}

}
