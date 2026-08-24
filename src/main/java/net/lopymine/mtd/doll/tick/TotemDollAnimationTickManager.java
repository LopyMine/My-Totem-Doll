package net.lopymine.mtd.doll.tick;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.*;
import net.lopymine.mtd.model.bb.BBAnimation.BBAnimationEntry;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollAnimationTickManager {

	private static final int EXPIRE_TICKS = 20;

	private final Map<TotemDollAnimationKey, TotemDollAnimationState> states = new ConcurrentHashMap<>();
	private int currentTick;

	private static final TotemDollAnimationTickManager INSTANCE = new TotemDollAnimationTickManager();

	public static TotemDollAnimationTickManager getInstance() {
		return INSTANCE;
	}

	public void tick() {
		this.currentTick++;

		Iterator<TotemDollAnimationState> iterator = this.states.values().iterator();
		while (iterator.hasNext()) {
			TotemDollAnimationState state = iterator.next();
			if (this.currentTick - state.getLastSeenTick() > EXPIRE_TICKS) {
				iterator.remove();
				continue;
			}
			state.tick();
		}
	}

	public TotemDollAnimationState getOrCreate(@NotNull TotemDollAnimationKey key, @NotNull BBAnimationEntry entry) {
		TotemDollAnimationState state = this.states.computeIfAbsent(key, (createdKey) -> new TotemDollAnimationState(createdKey.animationId(), entry.isLoop(), this.getLengthInTicks(entry), this.currentTick));
		state.setLastSeenTick(this.currentTick);
		return state;
	}

	public float getAnimationTicks(@NotNull TotemDollAnimationKey key, @NotNull BBAnimationEntry entry, float partialTick) {
		return this.getOrCreate(key, entry).getAnimationTicks(partialTick);
	}

	public int getLengthInTicks(@NotNull BBAnimationEntry entry) {
		return Math.max(entry.getAnimationLength() * 20, 1);
	}

	@Nullable
	public TotemDollAnimationState get(@NotNull TotemDollAnimationKey key) {
		return this.states.get(key);
	}

	public void restart(@NotNull TotemDollAnimationKey key) {
		TotemDollAnimationState state = this.states.get(key);
		if (state != null) {
			state.restart();
		}
	}

	public void remove(@NotNull TotemDollAnimationKey key) {
		states.remove(key);
	}

	public void clear() {
		states.clear();
	}
}
