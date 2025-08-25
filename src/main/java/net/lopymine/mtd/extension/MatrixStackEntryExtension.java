package net.lopymine.mtd.extension;

import net.minecraft.client.util.math.MatrixStack.Entry;

public class MatrixStackEntryExtension {

	public static void copyFrom(Entry entry, Entry anotherEntry) {
		entry.getPositionMatrix().set(anotherEntry.getPositionMatrix());
		entry.getNormalMatrix().set(anotherEntry.getNormalMatrix());
		//? if >=1.21 {
		entry.canSkipNormalization = anotherEntry.canSkipNormalization;
		//?}
	}

}
