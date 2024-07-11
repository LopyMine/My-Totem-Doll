package net.lopymine.mtd.modmenu.yacl.simple;

import java.util.function.BooleanSupplier;

public class SimpleCollector {

	private SimpleCollector() {
	}

	public static <T> T getIf(T value, BooleanSupplier condition) {
		if (condition.getAsBoolean()) {
			return value;
		}
		return null;
	}
}
