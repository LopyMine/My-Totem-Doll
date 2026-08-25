package net.lopymine.mtd.doll.tick;

import java.util.UUID;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TotemDollAnimationKey(@NotNull String sourceId, @NotNull DollRenderContext renderContext, @NotNull String animationId) {

	public static TotemDollAnimationKey of(@NotNull String sourceId, @NotNull DollRenderContext renderContext, @NotNull String animationId) {
		return new TotemDollAnimationKey(sourceId, renderContext, animationId);
	}

	public static String sourceOf(@NotNull Entity entity) {
		return "entity:" + entity.getUUID();
	}

	public static String sourceOf(@NotNull UUID uuid) {
		return "entity:" + uuid;
	}

	public static String sourceOf(@NotNull BlockPos pos) {
		return "block:" + pos.asLong();
	}

	public static String sourceOf(@NotNull String prefix, int x, int y) {
		return prefix + ":" + x + ":" + y;
	}

	public static String sourceOf(@NotNull DollRenderContext renderContext) {
		return "context:" + renderContext.getId();
	}

	public static String sourceOfGui(int x, int y) {
		Screen screen = Minecraft.getInstance().gui.screen();
		return sourceOf("gui:" + (screen == null ? "hud" : screen.getClass().getName()), x, y);
	}
}
