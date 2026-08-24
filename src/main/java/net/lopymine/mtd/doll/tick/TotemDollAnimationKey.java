package net.lopymine.mtd.doll.tick;

import java.util.UUID;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record TotemDollAnimationKey(@NotNull String sourceId, @NotNull DollRenderContext renderContext, @NotNull String animationId) {

	public static final String SOURCE_PREVIEW = "preview";
	public static final String SOURCE_TOOLTIP = "tooltip";

	public static TotemDollAnimationKey of(String sourceId, DollRenderContext renderContext, String animationId) {
		return new TotemDollAnimationKey(sourceId, renderContext, animationId);
	}

	public static TotemDollAnimationKey of(Entity entity, DollRenderContext renderContext, String animationId) {
		return new TotemDollAnimationKey(sourceOf(entity), renderContext, animationId);
	}

	public static TotemDollAnimationKey of(UUID playerUuid, DollRenderContext renderContext, String animationId) {
		return new TotemDollAnimationKey(sourceOf(playerUuid), renderContext, animationId);
	}

	public static TotemDollAnimationKey of(BlockPos pos, DollRenderContext renderContext, String animationId) {
		return new TotemDollAnimationKey(sourceOf(pos), renderContext, animationId);
	}

	public static TotemDollAnimationKey ofSlot(String screenId, int slot, DollRenderContext renderContext, String animationId) {
		return new TotemDollAnimationKey(sourceOfSlot(screenId, slot), renderContext, animationId);
	}

	public static String sourceOf(Entity entity) {
		return "entity:" + entity.getUUID();
	}

	public static String sourceOf(UUID playerUuid) {
		return "player:" + playerUuid;
	}

	public static String sourceOf(BlockPos pos) {
		return "block:" + pos.asLong();
	}

	public static String sourceOfSlot(String screenId, int slot) {
		return "slot:" + screenId + ":" + slot;
	}
}
