package net.lopymine.mtd.model.base;

import java.util.*;
import java.util.Map.Entry;
import lombok.Getter;
import net.lopymine.mtd.model.bb.BBAnimation.*;
import net.lopymine.mtd.utils.easing.EasingInterpolation;
import net.minecraft.client.model.geom.PartPose;
import org.jetbrains.annotations.*;
import org.joml.Vector3f;

@Getter
public class MAnimation {

	private final String name;
	private final int lengthInTicks;
	private final boolean loop;
	private final List<MAnimationBone> bones;

	private MAnimation(String name, int lengthInTicks, boolean loop, List<MAnimationBone> bones) {
		this.name          = name;
		this.lengthInTicks = lengthInTicks;
		this.loop          = loop;
		this.bones         = bones;
	}

	@Nullable
	public static MAnimation bake(@NotNull String name, @NotNull BBAnimationEntry entry, @NotNull MModel root) {
		List<MAnimationBone> bones = new ArrayList<>();

		for (Entry<String, BBAnimationBone> boneEntry : entry.getBones().entrySet()) {
			MModel part = root.findModelByName(boneEntry.getKey());
			if (part == null) {
				continue;
			}

			BBAnimationBone animationBone = boneEntry.getValue();

			List<MAnimationChannel> channels = new ArrayList<>();
			addChannel(channels, MAnimationTarget.POSITION, animationBone.getPosition());
			addChannel(channels, MAnimationTarget.ROTATION, animationBone.getRotation());
			addChannel(channels, MAnimationTarget.SCALE, animationBone.getScale());

			if (channels.isEmpty()) {
				continue;
			}

			bones.add(new MAnimationBone(part, List.copyOf(channels)));
		}

		if (bones.isEmpty()) {
			return null;
		}

		return new MAnimation(name, getLengthInTicks(entry), entry.isLoop(), List.copyOf(bones));
	}

	public static int getLengthInTicks(@NotNull BBAnimationEntry entry) {
		return Math.max(Math.round(entry.getAnimationLength() * 20F), 1);
	}

	private static void addChannel(List<MAnimationChannel> channels, MAnimationTarget target, @Nullable BBKeyframes keyframes) {
		if (keyframes == null) {
			return;
		}

		NavigableMap<Integer, BBKeyframeEntry> compiledMap = keyframes.getCompiledMap();
		if (compiledMap.isEmpty()) {
			return;
		}

		MAnimationKeyframe[] bakedKeyframes = new MAnimationKeyframe[compiledMap.size()];

		int index = 0;
		for (Entry<Integer, BBKeyframeEntry> entry : compiledMap.entrySet()) {
			bakedKeyframes[index] = MAnimationKeyframe.bake(entry.getKey(), entry.getValue());
			index++;
		}

		channels.add(new MAnimationChannel(target, bakedKeyframes));
	}

	public void apply(float ticks) {
		float time = this.loop ? ticks % this.lengthInTicks : Math.min(ticks, this.lengthInTicks);
		for (MAnimationBone bone : this.bones) {
			bone.apply(time);
		}
	}

	public void reset() {
		for (MAnimationBone bone : this.bones) {
			bone.reset();
		}
	}

	public enum MAnimationTarget {

		POSITION {
			@Override
			public void apply(MModel part, Vector3f vector) {
				PartPose pose = part.getInitialPose();
				part.x = pose.x() + vector.x;
				part.y = pose.y() + vector.y * -1;
				part.z = pose.z() + vector.z;
			}
		},

		ROTATION {
			@Override
			public void apply(MModel part, Vector3f vector) {
				PartPose pose = part.getInitialPose();
				part.xRot = pose.xRot() + (float) Math.toRadians(vector.x);
				part.yRot = pose.yRot() + (float) Math.toRadians(vector.y);
				part.zRot = pose.zRot() + (float) Math.toRadians(vector.z);
			}
		},

		SCALE {
			@Override
			public void apply(MModel part, Vector3f vector) {
				part.xScale = vector.x;
				part.yScale = vector.y;
				part.zScale = vector.z;
			}
		};

		public abstract void apply(MModel part, Vector3f vector);
	}

	@Getter
	public static class MAnimationBone {

		private final MModel part;
		private final List<MAnimationChannel> channels;
		private final Vector3f transformation = new Vector3f();

		private final float restXScale;
		private final float restYScale;
		private final float restZScale;

		public MAnimationBone(MModel part, List<MAnimationChannel> channels) {
			this.part       = part;
			this.channels   = channels;
			this.restXScale = part.xScale;
			this.restYScale = part.yScale;
			this.restZScale = part.zScale;
		}

		public void apply(float ticks) {
			this.reset();
			for (MAnimationChannel channel : this.channels) {
				channel.apply(this.part, ticks, this.transformation);
			}
		}

		public void reset() {
			PartPose pose = this.part.getInitialPose();

			this.part.x    = pose.x();
			this.part.y    = pose.y();
			this.part.z    = pose.z();
			this.part.xRot = pose.xRot();
			this.part.yRot = pose.yRot();
			this.part.zRot = pose.zRot();

			this.part.xScale = this.restXScale;
			this.part.yScale = this.restYScale;
			this.part.zScale = this.restZScale;
		}
	}

	public record MAnimationChannel(MAnimationTarget target, MAnimationKeyframe[] keyframes) {

		public void apply(MModel part, float ticks, Vector3f transformation) {
			if (this.keyframes[0].time() > ticks) {
				return;
			}

			int index = this.findIndex(ticks);
			MAnimationKeyframe current = this.keyframes[index];

			if (index == this.keyframes.length - 1) {
				this.target.apply(part, transformation.set(current.vector()));
				return;
			}

			MAnimationKeyframe next = this.keyframes[index + 1];
			float progress = (ticks - current.time()) / (float) (next.time() - current.time());

			this.target.apply(part, next.interpolateFrom(current, progress, transformation));
		}

		private int findIndex(float ticks) {
			int index = 0;
			for (int i = 0; i < this.keyframes.length; i++) {
				if (this.keyframes[i].time() > ticks) {
					break;
				}
				index = i;
			}
			return index;
		}
	}

	public record MAnimationKeyframe(int time, @NotNull Vector3f vector, @NotNull EasingInterpolation easing) {

		public static MAnimationKeyframe bake(int time, @NotNull BBKeyframeEntry entry) {
			EasingInterpolation easing = entry.getEasing();
			return new MAnimationKeyframe(
					time,
					new Vector3f(entry.getVector()),
					easing == null ? EasingInterpolation.LINEAR_INTERPOLATION : easing
			);
		}

		public Vector3f interpolateFrom(@NotNull MAnimationKeyframe previous, float progress, @NotNull Vector3f transformation) {
			return transformation.set(
					(float) this.easing.getInterpolated(previous.vector.x, this.vector.x, progress),
					(float) this.easing.getInterpolated(previous.vector.y, this.vector.y, progress),
					(float) this.easing.getInterpolated(previous.vector.z, this.vector.z, progress)
			);
		}
	}
}
