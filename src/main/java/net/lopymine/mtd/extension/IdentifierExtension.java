package net.lopymine.mtd.extension;

import net.minecraft.resources.Identifier;

public class IdentifierExtension {

	public static String getFileName(Identifier identifier) {
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

	public static Identifier getFolderId(Identifier identifier) {
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
