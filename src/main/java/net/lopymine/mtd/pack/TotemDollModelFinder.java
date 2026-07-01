package net.lopymine.mtd.pack;

import java.util.*;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.resources.ResourceManager;

public class TotemDollModelFinder {

	private static final Set<ResourceLocation> BUILTIN_TOTEM_MODELS = new LinkedHashSet<>();
	private static final Map<String, Set<ResourceLocation>> FOUNDED_TOTEM_MODELS = new LinkedHashMap<>();

	public static Map<String, Set<ResourceLocation>> getFoundedTotemModels() {
		return FOUNDED_TOTEM_MODELS;
	}

	public static Set<ResourceLocation> getBuiltinTotemModels() {
		return BUILTIN_TOTEM_MODELS;
	}

	public static void reload(ResourceManager resourceManager) {
		List<PackResources> list = resourceManager.listPacks().filter(resourcePack -> resourcePack.getNamespaces(PackType.CLIENT_RESOURCES).contains(MyTotemDoll.MOD_ID)).toList();

		FOUNDED_TOTEM_MODELS.clear();
		for (PackResources pack : list) {
			String packId = pack.packId().replace("file/", "").replace("mod/", "");
			if (packId.equals(MyTotemDoll.MOD_ID)) {
				continue;
			}
			pack.listResources(PackType.CLIENT_RESOURCES, MyTotemDoll.MOD_ID, "dolls", (id, input) -> {
				if (!isModelPath(id)) {
					return;
				}

				Set<ResourceLocation> set = FOUNDED_TOTEM_MODELS.getOrDefault(packId, new LinkedHashSet<>());
				set.add(id);

				if (!FOUNDED_TOTEM_MODELS.containsKey(packId)) {
					FOUNDED_TOTEM_MODELS.put(packId, set);
				}
			});
		}
	}

	private static boolean isModelPath(ResourceLocation id) {
		return id.getPath().endsWith(".bbmodel");
	}
}
