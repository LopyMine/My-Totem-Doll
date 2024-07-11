package net.lopymine.mtd.client.command;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.lopymine.mtd.client.command.reload.RefreshCommand;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MyTotemDollCommandManager {

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("my-totem-doll")
					.then(RefreshCommand.getInstance()));
		});
	}
}
