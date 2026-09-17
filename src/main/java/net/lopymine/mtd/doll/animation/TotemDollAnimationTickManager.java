package net.lopymine.mtd.doll.animation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.*;
import net.lopymine.mtd.config.resourcepack.AnimatedDollConfig;
import net.lopymine.mtd.config.resourcepack.AnimatedDollConfig.AnimationReference;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.BBAnimation;
import net.lopymine.mtd.model.bb.BBAnimation.BBAnimationEntry;
import net.lopymine.mtd.model.bb.manager.BlockBenchAnimationsManager;
import net.lopymine.mtd.pack.manager.AnimatedDollConfigsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollAnimationTickManager {

	private static final int EXPIRE_TICKS = 20;
	private static final int RESTART_GAP_TICKS = 2;

	private static final TotemDollAnimationTickManager INSTANCE = new TotemDollAnimationTickManager();

	private final Map<TotemDollAnimationKey, TotemDollAnimationState> states = new ConcurrentHashMap<>();
	private int currentTick;

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

	public static float getPartialTick() {
		Minecraft client = Minecraft.getInstance();
		return client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

	public void prepare(@NotNull TotemDollData data, @NotNull DollRenderContext renderContext, @Nullable String sourceId) {
		TotemDollRenderProperties properties = data.getRenderProperties();
		properties.setAnimation(null);

		AnimatedDollConfig config = AnimatedDollConfigsManager.getConfig(properties.getAnimatedConfigId());
		if (config == null) {
			return;
		}

		Identifier modelId = config.getModelId(renderContext);
		if (modelId != null) {
			data.setFrameMModel(modelId);
		}

		properties.setAnimation(this.resolve(config, renderContext, sourceId));
	}

	@Nullable
	public MAnimationRequest resolve(@NotNull AnimatedDollConfig config, @NotNull DollRenderContext renderContext, @Nullable String sourceId) {
		AnimationReference reference = config.getAnimationReference(renderContext);
		if (reference == null) {
			return null;
		}

		BBAnimation animation = BlockBenchAnimationsManager.getRegisteredConfigs().get(reference.fileId());
		if (animation == null) {
			return null;
		}

		BBAnimationEntry entry = animation.getAnimations().get(reference.name());
		if (entry == null) {
			return null;
		}

		TotemDollAnimationKey key = TotemDollAnimationKey.of(sourceId == null ? TotemDollAnimationKey.context(renderContext) : sourceId, renderContext, reference.bakeKey());

		return new MAnimationRequest(reference.bakeKey(), reference.name(), entry, this.getAnimationTicks(key, entry));
	}

	public float getAnimationTicks(@NotNull TotemDollAnimationKey key, @NotNull BBAnimationEntry entry) {
		return this.getOrCreate(key, entry).getAnimationTicks(getPartialTick());
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

	public TotemDollAnimationState getOrCreate(@NotNull TotemDollAnimationKey key, @NotNull BBAnimationEntry entry) {
		TotemDollAnimationState state = this.states.computeIfAbsent(key, (createdKey) -> new TotemDollAnimationState(createdKey.animationId(), entry.isLoop(), MAnimation.getLengthInTicks(entry), this.currentTick));

		if (this.currentTick - state.getLastSeenTick() > RESTART_GAP_TICKS) {
			state.restart();
		}

		state.setLastSeenTick(this.currentTick);
		return state;
	}

	public void remove(@NotNull TotemDollAnimationKey key) {
		this.states.remove(key);
	}

	public void removeBySource(@NotNull String sourceId) {
		this.states.keySet().removeIf((key) -> key.sourceId().equals(sourceId));
	}

	public void clear() {
		this.states.clear();
	}
}
