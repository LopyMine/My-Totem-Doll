package net.lopymine.mtd.doll.animation;

import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
public class TotemDollAnimationState {

	@NotNull
	private final String animationId;
	private final boolean loop;
	private final int length;

	private int ticks;
	private boolean finished;
	@Setter
	private int lastSeenTick;

	public TotemDollAnimationState(@NotNull String animationId, boolean loop, int length, int lastSeenTick) {
		this.animationId = animationId;
		this.loop        = loop;
		this.length      = Math.max(length, 1);
		this.lastSeenTick = lastSeenTick;
	}

	public void tick() {
		if (this.finished) {
			return;
		}
		this.ticks++;
		if (this.ticks < this.length) {
			return;
		}
		if (this.loop) {
			this.ticks = 0;
			return;
		}
		this.ticks    = this.length;
		this.finished = true;
	}

	public float getAnimationTicks(float partialTick) {
		if (this.finished) {
			return this.length;
		}
		float value = this.ticks + partialTick;
		if (this.loop) {
			return value % this.length;
		}
		return Math.min(value, this.length);
	}

	public void restart() {
		this.ticks    = 0;
		this.finished = false;
	}
}
