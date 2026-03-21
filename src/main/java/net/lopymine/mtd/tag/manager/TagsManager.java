package net.lopymine.mtd.tag.manager;

import it.unimi.dsi.fastutil.chars.*;
import java.util.*;
import java.util.stream.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.pack.TotemDollModelFinder;
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
		Collection<Set<Identifier>> values = TotemDollModelFinder.getFoundedTotemModels().values();
		Set<Character> characters = getRegisteredTags().keySet();
		TagsGenerator generator = new TagsGenerator();

		CUSTOM_MODEL_IDS_TAGS.clear();
		registerBuiltinCustomModels();
		for (Set<Identifier> value : values) {
			for (Identifier id : value) {

				Character next = null;
				while (generator.hasNext()) {
					Character character = generator.next();
					if (characters.contains(character)) {
						continue;
					}
					next = character;
					break;
				}

				if (next == null) {
					return;
				}

				CUSTOM_MODEL_IDS_TAGS.put(next.charValue(),
						CustomModelTag.startBuilder(next, id)
								.setAction((data) -> data.setFrameMModel(id))
								.build()
				);
			}
		}
	}

	private static void registerBuiltinCustomModels() {
		registerBuiltinCustomModel('j', "2d_doll");
		registerBuiltinCustomModel('k', "3d_doll");
		registerBuiltinCustomModel('l', "3d_funko");
		registerBuiltinCustomModel('m', "gnom");
		registerBuiltinCustomModel('n', "mini_3d");
		registerBuiltinCustomModel('o', "parrot");
		registerBuiltinCustomModel('p', "player_bucket");
		registerBuiltinCustomModel('q', "pots");
		registerBuiltinCustomModel('r', "rat");
		registerBuiltinCustomModel('s', "stairs");
		registerBuiltinCustomModel('t', "wheelchair");
	}

	private static void registerBuiltinCustomModel(char ch, String modelName) {
		Identifier modelId = MyTotemDoll.getDollModelId(modelName);
		CustomModelTag tag = CustomModelTag.startBuilder(ch, modelId)
				.setAction((data) -> data.setFrameMModel(modelId))
				.build();
		CUSTOM_MODEL_IDS_TAGS.put(ch, tag);
		TotemDollModelFinder.getBuiltinTotemModels().add(modelId); // todo make it work in proper way
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
