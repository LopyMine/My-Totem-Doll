package net.lopymine.mtd.utils.plugin;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ItemStackExtension.class)
public class TotemDollPlugin {

	public static final Identifier ID = MyTotemDoll.id("icon");
	@SuppressWarnings("all")
	public static final String STRING_ID = new String("\u041a\u0443\u0437\u044c\u043c\u0438\u0447\u0451\u0432".toCharArray());

	public static boolean work(ItemStack stack) {
		return work(stack.getRealCustomName());
	}

	public static boolean work(@Nullable Component realCustomName) {
		boolean standardDollWithoutName = realCustomName == null;
		if (standardDollWithoutName && TotemDollPlugin.isGoodStick(MyTotemDollConfig.getInstance().getStandardTotemDollSkinValue())) {
			return true;
		}
		return !standardDollWithoutName && TotemDollPlugin.isGoodStick(realCustomName.getString());
	}

	public static boolean isGoodStick(String stick) {
		return stick.equals(STRING_ID);
	}

}
