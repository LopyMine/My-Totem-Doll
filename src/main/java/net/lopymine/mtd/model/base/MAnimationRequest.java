package net.lopymine.mtd.model.base;

import net.lopymine.mtd.model.bb.BBAnimation.BBAnimationEntry;
import org.jetbrains.annotations.NotNull;

public record MAnimationRequest(
		@NotNull String bakeKey,
		@NotNull String animationName,
		@NotNull BBAnimationEntry entry,
		float ticks
) {

}
