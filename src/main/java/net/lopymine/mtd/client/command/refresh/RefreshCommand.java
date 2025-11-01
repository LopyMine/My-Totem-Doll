package net.lopymine.mtd.client.command.refresh;

import java.util.Map;
import java.util.concurrent.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.lopymine.mtd.api.MojangAPI;
import net.lopymine.mtd.client.command.builder.CommandTextBuilder;
import net.lopymine.mtd.doll.manager.TotemDollManager;
import org.jetbrains.annotations.Nullable;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RefreshCommand {

	@Nullable
	private static CompletableFuture<Float> RELOADING_ALL_FUTURE = null;
	private static final Map<String, CompletableFuture<Float>> RELOADING_FUTURES = new ConcurrentHashMap<>();

	public static LiteralArgumentBuilder<FabricClientCommandSource> getInstance() {
		return literal("refresh")
				.then(literal("all")
						.executes(RefreshCommand::reloadAll))
				.then(literal("player")
						.then(argument("nickname", StringArgumentType.word())
								.suggests((context, builder) ->
										CommandSource.suggestMatching(TotemDollManager.getAllLoadedKeys(), builder))
								.executes(RefreshCommand::reloadForPlayer)
						));
	}

	private static int reloadAll(CommandContext<FabricClientCommandSource> context) {
		if (RELOADING_ALL_FUTURE != null) {
			return 0;
		}

		Text startFeedback = CommandTextBuilder.startBuilder("command.refresh.all.start").build();
		context.getSource().sendFeedback(startFeedback);

		RELOADING_ALL_FUTURE = TotemDollManager.reloadData((seconds) -> {
			Text endFeedback = CommandTextBuilder.startBuilder("command.refresh.all.end", seconds).build();
			MinecraftClient.getInstance().execute(() -> context.getSource().sendFeedback(endFeedback));
		}).whenComplete((r, e) -> {
			RELOADING_ALL_FUTURE = null;
			if (e != null) {
				MyTotemDollClient.LOGGER.error("Failed to refresh all doll data: ", e);
			}
		});

		MojangAPI.useFallbackAPI = false;

		return Command.SINGLE_SUCCESS;
	}

	private static int reloadForPlayer(CommandContext<FabricClientCommandSource> context) {
		String nickname = StringArgumentType.getString(context, "nickname");

		CompletableFuture<Float> future = RELOADING_FUTURES.get(nickname);
		if (future != null) {
			return 0;
		}

		Text startFeedback = CommandTextBuilder.startBuilder("command.refresh.player.start", nickname).build();
		context.getSource().sendFeedback(startFeedback);

		CompletableFuture<Float> f = TotemDollManager.reloadData(nickname, (seconds) -> {
			Text endFeedback = CommandTextBuilder.startBuilder("command.refresh.player.end", nickname, seconds).build();
			MinecraftClient.getInstance().execute(() -> context.getSource().sendFeedback(endFeedback));
		});

		if (f != null) {
			CompletableFuture<Float> fc = f.whenComplete((r, e) -> {
				RELOADING_FUTURES.remove(nickname);
				if (e != null) {
					MyTotemDollClient.LOGGER.error("Failed to refresh doll data for \"{}\": ", nickname, e);
				}
			});
			RELOADING_FUTURES.put(nickname, fc);
		}

		return Command.SINGLE_SUCCESS;
	}
}
