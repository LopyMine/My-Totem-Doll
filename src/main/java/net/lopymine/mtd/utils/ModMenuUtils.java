package net.lopymine.mtd.utils;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.modmenu.yacl.simple.SimpleContent;

import java.util.function.Function;

public class ModMenuUtils {

	private ModMenuUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getOptionKey(String optionId) {
		return String.format("%s.modmenu.option.%s", MyTotemDoll.MOD_ID, optionId);
	}

	public static String getCategoryKey(String categoryId) {
		return String.format("%s.modmenu.category.%s", MyTotemDoll.MOD_ID, categoryId);
	}

	public static String getGroupKey(String groupId) {
		return String.format("%s.modmenu.group.%s", MyTotemDoll.MOD_ID, groupId);
	}

	public static Text getName(String key) {
		return Text.translatable(key + ".name");
	}

	public static Text getDescription(String key) {
		return Text.translatable(key + ".description");
	}

	public static Identifier getContentId(SimpleContent content, String contentId) {
		return MyTotemDoll.id(String.format("textures/config/%s/%s.%s", content.getFolder(), contentId, content.getFileExtension()));
	}

	public static Text getModTitle() {
		return MyTotemDoll.text("modmenu.title");
	}

	public static Function<Boolean, Text> getEnabledOrDisabledFormatter() {
		return state -> MyTotemDoll.text("modmenu.formatter.enabled_or_disabled." + state);
	}

	public static Text getNoConfigScreenMessage() {
		return MyTotemDoll.text("modmenu.no_config_library_screen.message");
	}

	public static Text getOldConfigScreenMessage(String version) {
		return MyTotemDoll.text("modmenu.old_config_library_screen.message", version, MyTotemDoll.YACL_DEPEND_VERSION);
	}
}
