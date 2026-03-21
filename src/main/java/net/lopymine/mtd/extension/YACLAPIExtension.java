package net.lopymine.mtd.extension;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.ListOption.Builder;
import java.util.List;

public class YACLAPIExtension {

	public static <A> void bindingE(Builder<A> builder, Binding<List<A>> binding, boolean instant) {
		builder.state(instant ? StateManager.createInstant(binding) : StateManager.createSimple(binding));
	}

	public static <A> void bindingE(Option.Builder<A> builder, Binding<A> binding, boolean instant) {
		builder.stateManager(instant ? StateManager.createInstant(binding) : StateManager.createSimple(binding));
	}

}
