package net.lopymine.mossy.utils;

import net.minecraft.util.Identifier;

import net.lopymine.mossy.Mossy;
import net.lopymine.mossy.modmenu.yacl.simple.SimpleContent;

public class ModMenuUtils {

	private ModMenuUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String getGroupTitleKey(String groupId) {
		return String.format("%s.modmenu.%s", Mossy.MOD_ID, groupId);
	}

	public static String getOptionKey(String groupId, String optionId) {
		return String.format("%s.modmenu.%s.option.%s", Mossy.MOD_ID, groupId, optionId);
	}

	public static String getDescriptionKey(String key) {
		return key + ".description";
	}

	public static Identifier getContentId(SimpleContent content, String optionId) {
		return Mossy.id(String.format("textures/config/%s/%s.%s", content.getFolder(), optionId, content.getFileExtension()));
	}
}
