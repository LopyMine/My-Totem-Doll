package net.lopymine.mtd.extension;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationExtension {

	public static String getFileName(ResourceLocation identifier) {
		String path = identifier.getPath();
		int i = path.lastIndexOf("/");
		if (i == -1) {
			return path;
		}
		if (i + 1 >= path.length()) {
			return path;
		}
		return path.substring(i + 1);
	}

	public static ResourceLocation getFolderId(ResourceLocation identifier) {
		String path = identifier.getPath();
		int i = path.lastIndexOf("/");
		if (i == -1) {
			return identifier;
		}
		if (i + 1 >= path.length()) {
			return identifier;
		}
		return identifier.withPath(path.substring(0, i + 1));
	}

}
