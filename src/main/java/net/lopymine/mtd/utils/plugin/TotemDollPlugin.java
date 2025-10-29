package net.lopymine.mtd.utils.plugin;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.loader.api.FabricLoader;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ItemStackExtension;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(ItemStackExtension.class)
public class TotemDollPlugin {

	public static final Identifier ID = /*? >=1.21.3 {*/MyTotemDoll.id("icon"); /*?} else {*/ /*MyTotemDoll.id("item/icon"); *//*?}*/
	@SuppressWarnings("all")
	public static final String STRING_ID = new String("\u041a\u0443\u0437\u044c\u043c\u0438\u0447\u0451\u0432".toCharArray());

	public static boolean work(ItemStack stack) {
		return work(stack.getRealCustomName());
	}

	public static boolean work(@Nullable Text realCustomName) {
		boolean standardDollWithoutName = realCustomName == null;
		if (standardDollWithoutName && TotemDollPlugin.isGoodStick(MyTotemDollConfig.getInstance().getStandardTotemDollSkinValue())) {
			return true;
		}
		if (!standardDollWithoutName && TotemDollPlugin.isGoodStick(realCustomName.getString())) {
			return true;
		}
		return false;
	}

	public static boolean isGoodStick(String stick) {
		return stick.equals(STRING_ID);
	}

	public static void register() {
		//? if <=1.21.4 {
		/*net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin.register(context -> {
			context.addModels(ID);
		});
		*///?}
	}

}
