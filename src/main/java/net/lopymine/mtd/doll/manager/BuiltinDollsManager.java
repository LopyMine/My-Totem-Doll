package net.lopymine.mtd.doll.manager;

import it.unimi.dsi.fastutil.chars.*;
import java.util.*;
import java.util.stream.Collectors;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.doll.data.BuiltinDoll;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

public class BuiltinDollsManager {

	private static final Char2ObjectMap<BuiltinDoll> REGISTERED_DOLLS = new Char2ObjectLinkedOpenHashMap<>();

	public static void register() {
		registerModel('j', "2d_doll");
		registerModel('k', "3d_doll");
		registerModel('l', "3d_funko");
		registerModel('m', "gnom");
		registerModel('n', "mini_3d");
		registerModel('o', "parrot");
		registerModel('p', "player_bucket");
		registerModel('q', "pots");
		registerModel('r', "rat");
		registerModel('s', "stairs");
		registerModel('t', "wheelchair");

		if (MyTotemDoll.ANIMATIONS_ENABLED) {
			registerAnimatedDoll('u', "punchy_3d_doll");
		}
	}

	public static void registerModel(char tag, String modelName) {
		registerDoll(new BuiltinDoll(tag, MyTotemDoll.getDollModelId(modelName), false));
	}

	public static void registerAnimatedDoll(char tag, String configName) {
		registerDoll(new BuiltinDoll(tag, MyTotemDoll.getAnimatedDollId(configName), true));
	}

	public static void registerDoll(@NotNull BuiltinDoll doll) {
		BuiltinDoll previous = REGISTERED_DOLLS.put(doll.tag(), doll);
		if (previous != null) {
			MyTotemDoll.LOGGER.warn("Builtin doll \"{}\" replaced \"{}\", both are registered with tag '{}'", doll.id(), previous.id(), doll.tag());
		}
	}

	@Nullable
	public static BuiltinDoll getDoll(char tag) {
		return REGISTERED_DOLLS.get(tag);
	}

	public static Collection<BuiltinDoll> getRegisteredDolls() {
		return REGISTERED_DOLLS.values();
	}

	public static Set<Identifier> getModelIds() {
		return getIds(false);
	}

	public static Set<Identifier> getAnimatedDollIds() {
		return getIds(true);
	}

	private static Set<Identifier> getIds(boolean animated) {
		return REGISTERED_DOLLS.values().stream()
				.filter((doll) -> doll.animated() == animated)
				.map(BuiltinDoll::id)
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
