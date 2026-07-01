package net.lopymine.mtd.extension;

import dev.isxander.yacl3.api.*;
import java.util.*;
import net.lopymine.mtd.loader.MyTotemDollLoader;

public class YACLAPIExtension {

	private static final String STATE_MANAGER_VERSION = "3.6.0";

	public static <A> ListOption.Builder<A> bindingE(ListOption.Builder<A> builder, Binding<List<A>> binding, boolean instant) {
		if (isStateManagerSupported()) {
			builder.state(instant ? StateManager.createInstant(binding) : StateManager.createSimple(binding));
		} else {
			builder.binding(binding);
		}

		return builder;
	}

	public static <A> Option.Builder<A> bindingE(Option.Builder<A> builder, Binding<A> binding, boolean instant) {
		if (isStateManagerSupported()) {
			builder.stateManager(instant ? StateManager.createInstant(binding) : StateManager.createSimple(binding));
		} else {
			builder.binding(binding);
			builder.instant(instant);
		}

		return builder;
	}

	private static boolean isStateManagerSupported() {
		String currentVersion = MyTotemDollLoader.getModVersion("yet_another_config_lib_v3", false);
		return MyTotemDollLoader.compareVersions(currentVersion, STATE_MANAGER_VERSION) >= 0;
	}
}
