package net.lopymine.mtd.pack;

import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;

import java.util.*;

public class TotemDollModelFinder {

	private static final Set<Identifier> BUILTIN_TOTEM_MODELS = new LinkedHashSet<>();
	private static final Map<String, Set<Identifier>> FOUNDED_TOTEM_MODELS = new LinkedHashMap<>();

	public static Map<String, Set<Identifier>> getFoundedTotemModels() {
		return FOUNDED_TOTEM_MODELS;
	}

	public static Set<Identifier> getBuiltinTotemModels() {
		return BUILTIN_TOTEM_MODELS;
	}

	public static void reload(ResourceManager resourceManager) {
		List<ResourcePack> list = resourceManager.streamResourcePacks().filter(resourcePack -> resourcePack.getNamespaces(ResourceType.CLIENT_RESOURCES).contains(MyTotemDoll.MOD_ID)).toList();

		FOUNDED_TOTEM_MODELS.clear();
		for (ResourcePack pack : list) {
			String packId = pack./*? if >=1.21 {*/getId()/*?} else {*//*getName()*//*?}*/.replace("file/", "");
			if (packId.equals(MyTotemDoll.MOD_ID) /*? if =1.20.1 {*/ /*|| pack instanceof net.fabricmc.fabric.impl.resource.loader.FabricModResourcePack *//*?}*/) {
				continue;
			}
			pack.findResources(ResourceType.CLIENT_RESOURCES, MyTotemDoll.MOD_ID, "dolls", (id, input) -> {
				if (!isModelPath(id)) {
					return;
				}

				Set<Identifier> set = FOUNDED_TOTEM_MODELS.getOrDefault(packId, new LinkedHashSet<>());
				set.add(id);

				if (!FOUNDED_TOTEM_MODELS.containsKey(packId)) {
					FOUNDED_TOTEM_MODELS.put(packId, set);
				}
			});
		}
	}

	private static boolean isModelPath(Identifier id) {
		return id.getPath().endsWith(".bbmodel");
	}
}
