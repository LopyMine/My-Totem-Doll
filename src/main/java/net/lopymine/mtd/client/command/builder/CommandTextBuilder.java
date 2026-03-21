package net.lopymine.mtd.client.command.builder;

import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.network.chat.*;

public class CommandTextBuilder {

	private static final MutableComponent MOD_ID_TEXT = MyTotemDoll.text("command.id");

	private final MutableComponent text;

	private CommandTextBuilder(String key, Object... args) {
		this.text = CommandTextBuilder.translatable(key, args);
	}

	private static MutableComponent translatable(String key, Object... args) {
		for (int i = 0; i < args.length; ++i) {
			Object object = args[i];
			if (!isPrimitive(object) && !(object instanceof Component)) {
				args[i] = String.valueOf(object);
			}
		}

		return MyTotemDoll.text(key, args);
	}

	private static boolean isPrimitive(Object object) {
		return object instanceof Number || object instanceof Boolean || object instanceof String;
	}

	public static CommandTextBuilder startBuilder(String key, Object... args) {
		return new CommandTextBuilder(key, args);
	}

	public Component build() {
		return MOD_ID_TEXT.copy().append(" ").append(this.text);
	}
}
