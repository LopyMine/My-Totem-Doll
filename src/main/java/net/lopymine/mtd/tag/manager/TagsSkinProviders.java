package net.lopymine.mtd.tag.manager;

import net.lopymine.mtd.doll.data.TotemDollData;


import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.skin.provider.SkinProvider;
import net.lopymine.mtd.skin.provider.extended.NameMCSkinProvider;
import java.util.*;
import org.jetbrains.annotations.Nullable;


public class TagsSkinProviders {

	private static final Map<String, SkinProvider> SKIN_PROVIDERS_IDS = new HashMap<>();

	public static Map<String, SkinProvider> getSkinProvidersIds() {
		return SKIN_PROVIDERS_IDS;
	}

	public static void register() {
		registerProvider("NameMC", NameMCSkinProvider.getInstance());
	}

	public static void registerProvider(String id, SkinProvider provider) {
		SKIN_PROVIDERS_IDS.put(id, provider);
	}

	public static boolean isProvider(String o) {
		int b = o.lastIndexOf("|");
		if (b == -1) {
			return SKIN_PROVIDERS_IDS.containsKey(o);
		}
		String[] split = o.substring(0, b).split("\\|");
		String id = split[0].trim();
		return SKIN_PROVIDERS_IDS.containsKey(id);
	}

	@Nullable
	public static SkinProvider getProviderFor(String o) {
		return SKIN_PROVIDERS_IDS.get(o);
	}

	public static TotemDollData loadDollFromProvider(String o) {
		if (!o.contains("|")) {
			return StandardTotemDollManager.getStandardDoll();
		}
		String[] split = o.split("\\|");
		String id = split[0].trim();
		SkinProvider skinProvider = SKIN_PROVIDERS_IDS.get(id);
		if (skinProvider == null || split.length < 2) {
			return StandardTotemDollManager.getStandardDoll();
		}
		return skinProvider.getOrLoadDoll(split[1].trim());
	}
}
