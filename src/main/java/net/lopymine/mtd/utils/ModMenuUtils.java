package net.lopymine.mtd.utils;

import java.util.function.Function;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.yacl.custom.simple.utils.SimpleContent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ModMenuUtils {

	private ModMenuUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getOptionKey(String optionId) {
		return String.format("modmenu.option.%s", optionId);
	}

	public static String getCategoryKey(String categoryId) {
		return String.format("modmenu.category.%s", categoryId);
	}

	public static String getGroupKey(String groupId) {
		return String.format("modmenu.group.%s", groupId);
	}

	public static Component getName(String key) {
		return MyTotemDoll.text(key + ".name");
	}

	public static Component getDescription(String key) {
		return MyTotemDoll.text(key + ".description");
	}

	public static Identifier getContentId(SimpleContent content, String contentId) {
		return MyTotemDoll.id(String.format("textures/config/%s.%s", contentId, content.getFileExtension()));
	}

	public static Component getModTitle() {
		return MyTotemDoll.text("modmenu.title");
	}

	public static Function<Boolean, Component> getEnabledOrDisabledFormatter() {
		return state -> MyTotemDoll.text("modmenu.formatter.enabled_or_disabled." + state);
	}

	public static Component getNoConfigScreenMessage() {
		return MyTotemDoll.text("modmenu.no_config_library_screen.message");
	}

	public static Component getOldConfigScreenMessage(String version) {
		return MyTotemDoll.text("modmenu.old_config_library_screen.message", version, MyTotemDoll.YACL_DEPEND_VERSION);
	}
}
