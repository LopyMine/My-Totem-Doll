package net.lopymine.mtd.doll.tag;

import net.lopymine.mtd.doll.model.DollModel;
import net.lopymine.mtd.doll.tag.action.TagAction;

import java.util.*;

public class DollTagManager {

	public static final String MINECRAFT_NICKNAME_WITH_TAGS_REGEX = "^\\w{2,16} \\| \\w+$"; // LopyMine | abc
	private static final Map<Character, TagAction> TAGS = new HashMap<>();

	public static void registerTag(char tag, TagAction action) {
		TAGS.put(tag, action);
	}

	public static void register() {
		registerTag('a', (model) -> {
			System.out.println("bruh");
		});
	}

	public static String getOnlyNickname(String nickname) {
		if (nickname.contains("|")) {
			return nickname.split("\\|")[0].trim().toLowerCase();
		}
		return nickname.toLowerCase();
	}

	public static boolean canBeProcessed(String text) {
		return text.matches(MINECRAFT_NICKNAME_WITH_TAGS_REGEX);
	}

	public static void process(String nickname, DollModel model) {
		if (!nickname.contains("|")) {
			return;
		}
		String[] strings = nickname.split("\\|");
		if (strings.length < 2) {
			return;
		}
		strings[1].trim().chars().mapToObj(i -> (char) i).distinct().forEach((character) -> {
			TagAction tagAction = TAGS.get(character);
			if (tagAction == null) {
				return;
			}
			tagAction.process(model);
		});
	}
}
