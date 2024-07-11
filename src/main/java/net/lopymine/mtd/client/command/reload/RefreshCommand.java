package net.lopymine.mtd.client.command.reload;

import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.lopymine.mtd.cache.CachedSkinsManager;
import net.lopymine.mtd.client.command.builder.CommandTextBuilder;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RefreshCommand {

	public static LiteralArgumentBuilder<FabricClientCommandSource> getInstance() {
		return literal("refresh")
				.then(literal("all")
						.executes(RefreshCommand::reloadAll))
				.then(literal("player")
						.then(argument("nickname", StringArgumentType.word())
								.suggests((context, builder) -> CommandSource.suggestMatching(CachedSkinsManager.getLoadedPlayers(), builder))
								.executes(RefreshCommand::reloadForPlayer)
						));
	}

	private static int reloadAll(CommandContext<FabricClientCommandSource> context) {
		Text startFeedback = CommandTextBuilder.startBuilder("command.refresh.all.start").build();
		context.getSource().sendFeedback(startFeedback);

		CachedSkinsManager.reload((seconds) -> {
			Text endFeedback = CommandTextBuilder.startBuilder("command.refresh.all.end", seconds).build();
			context.getSource().sendFeedback(endFeedback);
		});

		return Command.SINGLE_SUCCESS;
	}

	private static int reloadForPlayer(CommandContext<FabricClientCommandSource> context) {
		String nickname = StringArgumentType.getString(context, "nickname");
		Text startFeedback = CommandTextBuilder.startBuilder("command.refresh.player.start", nickname).build();
		context.getSource().sendFeedback(startFeedback);

		CachedSkinsManager.reload(nickname, (seconds) -> {
			Text feedback = CommandTextBuilder.startBuilder("command.refresh.player.end", nickname, seconds).build();
			context.getSource().sendFeedback(feedback);
		});

		return Command.SINGLE_SUCCESS;
	}
}
