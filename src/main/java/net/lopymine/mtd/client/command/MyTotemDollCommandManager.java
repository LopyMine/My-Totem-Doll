//~ client_fabric_commands

package net.lopymine.mtd.client.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.lopymine.mtd.client.command.refresh.RefreshCommand;
import static net.lopymine.mtd.utils.CommandUtils.literal;

public class MyTotemDollCommandManager {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("my-totem-doll")
				.then(RefreshCommand.getInstance()));
	}
}
