//~ client_fabric_commands

package net.lopymine.mtd.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.lopymine.mtd.client.command.refresh.RefreshCommand;
import static net.lopymine.mtd.utils.CommandUtils.literal;

public class MyTotemDollCommandManager {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(literal("my-totem-doll")
				.then(RefreshCommand.getInstance()));
	}
}
