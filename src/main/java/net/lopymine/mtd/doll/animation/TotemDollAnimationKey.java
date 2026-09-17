package net.lopymine.mtd.doll.animation;

import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TotemDollAnimationKey(@NotNull String sourceId, @NotNull DollRenderContext renderContext, @NotNull String animationId) {

	public static TotemDollAnimationKey of(@NotNull String sourceId, @NotNull DollRenderContext renderContext, @NotNull String animationId) {
		return new TotemDollAnimationKey(sourceId, renderContext, animationId);
	}

	public static String entity(@NotNull Entity entity) {
		return "entity:" + entity.getUUID();
	}

	public static String ui(@NotNull String prefix, int x, int y) {
		return prefix + ":" + x + ":" + y;
	}

	public static String context(@NotNull DollRenderContext renderContext) {
		return "context:" + renderContext.getId();
	}

	public static String gui(int x, int y) {
		Screen screen = Minecraft.getInstance().gui.screen();
		return ui("gui:" + (screen == null ? "hud" : screen.getClass().getName()), x, y);
	}
}
