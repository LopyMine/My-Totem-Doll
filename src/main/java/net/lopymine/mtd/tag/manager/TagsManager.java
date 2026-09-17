package net.lopymine.mtd.tag.manager;

import it.unimi.dsi.fastutil.chars.*;
import java.util.*;
import java.util.stream.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.resourcepack.AnimatedDollConfig;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.manager.BuiltinDollsManager;
import net.lopymine.mtd.pack.TotemDollModelFinder;
import net.lopymine.mtd.pack.manager.AnimatedDollConfigsManager;
import net.lopymine.mtd.tag.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

public class TagsManager {

	private static final Char2ObjectMap<CustomModelTag> CUSTOM_MODEL_IDS_TAGS = new Char2ObjectArrayMap<>();
	private static final Char2ObjectMap<Tag> PREPROCESSOR_TAGS = new Char2ObjectArrayMap<>();
	private static final Char2ObjectMap<Tag> POSTPROCESSOR_TAGS = new Char2ObjectArrayMap<>();

	public static Char2ObjectMap<Tag> getRegisteredTags() {
		Char2ObjectMap<Tag> tags = new Char2ObjectLinkedOpenHashMap<>(PREPROCESSOR_TAGS);
		tags.putAll(POSTPROCESSOR_TAGS);
		return tags;
	}

	public static Map<Character, CustomModelTag> getCustomModelIdsTags() {
		return CUSTOM_MODEL_IDS_TAGS;
	}

	public static void register() {
		registerPreprocessorTag(
				Tag.startBuilder('0')
						.setAction((data) -> data.getRenderProperties().setSlim(true))
						.build()
		);

		registerPreprocessorTag(
				Tag.startBuilder('1')
						.setAction((data) -> data.getRenderProperties().setSlim(false))
						.build()
		);

		registerPostprocessorTag(
				Tag.startBuilder('2')
						.setAction((data) -> {
							data.getRenderProperties().disable(data.getModelToRender().getCape());
						})
						.build()
		);

		registerPostprocessorTag(
				Tag.startBuilder('3')
						.setAction((data) -> {
							data.getRenderProperties().disable(data.getModelToRender().getCape());
							data.getRenderProperties().enable(data.getModelToRender().getElytra());
						})
						.build()
		);


	}

	public static void reloadCustomModelIdsTags() {
		Set<Character> characters = getRegisteredTags().keySet();
		TagsGenerator generator = new TagsGenerator();
		Set<Identifier> registered = new HashSet<>();

		CUSTOM_MODEL_IDS_TAGS.clear();

		registerBuiltinDolls(registered);
		registerFoundedModels(generator, characters, registered);
		registerAnimatedDolls(generator, characters, registered);
	}

	private static void registerBuiltinDolls(Set<Identifier> registered) {
		for (BuiltinDoll doll : BuiltinDollsManager.getRegisteredDolls()) {
			boolean success = doll.animated() ? registerAnimatedDoll(doll.tag(), doll.id()) : registerModel(doll.tag(), doll.id());
			if (success) {
				registered.add(doll.id());
			}
		}
	}

	private static void registerFoundedModels(TagsGenerator generator, Set<Character> characters, Set<Identifier> registered) {
		for (Set<Identifier> modelIds : TotemDollModelFinder.getFoundedTotemModels().values()) {
			for (Identifier modelId : modelIds) {
				if (!registered.add(modelId)) {
					continue;
				}

				Character next = nextCharacter(generator, characters);
				if (next == null) {
					return;
				}

				registerModel(next, modelId);
			}
		}
	}

	private static void registerAnimatedDolls(TagsGenerator generator, Set<Character> characters, Set<Identifier> registered) {
		List<Identifier> configIds = AnimatedDollConfigsManager.getRegisteredConfigs().keySet().stream().sorted(Comparator.comparing(Identifier::toString)).toList();

		for (Identifier configId : configIds) {
			if (registered.contains(configId)) {
				continue;
			}

			Character next = nextCharacter(generator, characters);
			if (next == null) {
				return;
			}

			if (registerAnimatedDoll(next, configId)) {
				registered.add(configId);
			}
		}
	}

	private static boolean registerModel(char character, Identifier modelId) {
		CUSTOM_MODEL_IDS_TAGS.put(character,
				CustomModelTag.startBuilder(character, modelId)
						.setAction((data) -> data.setFrameMModel(modelId))
						.build()
		);
		return true;
	}

	private static boolean registerAnimatedDoll(char character, Identifier configId) {
		AnimatedDollConfig config = AnimatedDollConfigsManager.getRegisteredConfigs().get(configId);
		if (config == null) {
			MyTotemDoll.LOGGER.warn("Skipped animated doll \"{}\", no such config found", configId);
			return false;
		}

		Identifier modelId = config.getStandardModelId();
		if (modelId == null) {
			MyTotemDoll.LOGGER.warn("Skipped animated doll \"{}\", it has no \"standard_model\"", configId);
			return false;
		}

		CUSTOM_MODEL_IDS_TAGS.put(character,
				CustomModelTag.startAnimatedBuilder(character, configId, modelId)
						.setAction((data) -> data.setAnimatedDoll(configId))
						.build()
		);
		return true;
	}

	@Nullable
	private static Character nextCharacter(TagsGenerator generator, Set<Character> characters) {
		while (generator.hasNext()) {
			Character character = generator.next();
			if (characters.contains(character) || CUSTOM_MODEL_IDS_TAGS.containsKey(character.charValue())) {
				continue;
			}
			return character;
		}
		return null;
	}

	public static void registerPostprocessorTag(Tag tag) {
		POSTPROCESSOR_TAGS.put(tag.getTag(), tag);
	}

	public static void registerPreprocessorTag(Tag tag) {
		PREPROCESSOR_TAGS.put(tag.getTag(), tag);
	}

	public static String getNicknameOrSkinProviderFromName(String name) {
		return getDataFromString(name)[0];
	}

	@Nullable
	public static String getTagsFromName(String name) {
		return getDataFromString(name)[1];
	}

	public static String[] getDataFromString(String name) {
		String[] split = name.split("\\|");
		String o = split[0].trim();

		if (TagsSkinProviders.isProvider(o) && split.length >= 2) {
			String value = split[1].trim();
			String tags = split.length >= 3 ? split[2].trim() : null;
			return new String[]{joinData(o, value), tags};
		}

		String tags = split.length >= 2 ? split[1].trim() : null;
		return new String[]{o, tags};
	}

	public static void processTags(String tags, @NotNull TotemDollData data) {
		processCustomModelIdsTags(tags, data);
		processPreTags(tags, data);
		processPostTags(tags, data);
	}

	public static void processCustomModelIdsTags(String tags, TotemDollData data) {
		processTags(tags, data, CUSTOM_MODEL_IDS_TAGS);
	}

	public static void processPreTags(String tags, @NotNull TotemDollData data) {
		processTags(tags, data, PREPROCESSOR_TAGS);
	}

	public static void processPostTags(String tags, @NotNull TotemDollData data) {
		processTags(tags, data, POSTPROCESSOR_TAGS);
	}

	public static <E extends Tag> void processTags(String tags, @NotNull TotemDollData data, Char2ObjectMap<E> map) {
		getTags(tags).forEach((i) -> {
			Tag tag = map.get((char) i);
			if (tag == null) {
				return;
			}
			tag.process(data);
		});
	}

	@NotNull
	public static IntStream getRegisteredTags(String tags) {
		Char2ObjectMap<Tag> registeredTags = getRegisteredTags();
		return tags.trim().chars().filter((i) -> hasRegisteredTag(registeredTags, (char) i));
	}

	@NotNull
	public static IntStream getTags(String tags) {
		return tags.trim().chars();
	}

	public static String addTag(String string, Character tag) {
		String[] data = getDataFromString(string);
		if (data.length < 2) {
			return string;
		}

		String tags = data[1];
		String unsortedTags = tags == null ? String.valueOf(tag) : tags + tag;
		data[1] = sortTags(unsortedTags);
		return joinData(data);
	}

	private static String sortTags(String unsortedTags) {
		return getTags(unsortedTags).sorted().mapToObj((i) -> String.valueOf((char) i)).collect(Collectors.joining());
	}

	private static String joinData(String... data) {
		return String.join(" | ", data);
	}

	public static String removeTag(String name, Character tag) {
		String[] data = getDataFromString(name);
		if (data.length < 2) {
			return name;
		}
		String tags = data[1];
		data[1] = tags == null ? "" : tags.replace(String.valueOf(tag), "");

		if (data[1].isEmpty()) {
			return data[0].trim();
		}
		return joinData(data);
	}

	public static Identifier getTagIcon(char c) {
		if (hasRegisteredTag(CUSTOM_MODEL_IDS_TAGS, c)) {
			return MyTotemDoll.id("textures/gui/tags/unknown.png");
		}
		return MyTotemDoll.id("textures/gui/tags/%s.png".formatted(c));
	}

	public static Component getTagDescription(Character character) {
		return MyTotemDoll.text("tags.%s".formatted(character));
	}

	public static Component getAppliedTagDescription(char c) {
		return MyTotemDoll.text("tags.%s.applied".formatted(c));
	}

	@SuppressWarnings("all")
	public static boolean hasAnyTag(String tags) {
		return getRegisteredTags(tags).findFirst().isPresent();
	}

	public static <E extends Tag> boolean hasRegisteredTag(Char2ObjectMap<E> registeredTags, char c) {
		return registeredTags.containsKey(c);
	}
}
