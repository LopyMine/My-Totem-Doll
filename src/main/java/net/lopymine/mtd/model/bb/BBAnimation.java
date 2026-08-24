package net.lopymine.mtd.model.bb;

import com.mojang.serialization.Codec;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.utils.CodecUtils;
import static com.mojang.serialization.Codec.*;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class BBAnimation {

	public static final Codec<BBAnimation> CODEC = create((instance) -> instance.group(
			option("format_version", "", STRING, BBAnimation::getFormatVersion),
			option("animations", new HashMap<>(), Codec.unboundedMap(STRING, BBAnimationEntry.CODEC), BBAnimation::getAnimations)
	).apply(instance, BBAnimation::new));

	private String formatVersion;
	private Map<String, BBAnimationEntry> animations;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBAnimationEntry {

		public static final Codec<BBAnimationEntry> CODEC = create((instance) -> instance.group(
				option("loop", false, BOOL, BBAnimationEntry::isLoop),
				option("animation_length", 0, INT, BBAnimationEntry::getAnimationLength),
				option("bones", new HashMap<>(), Codec.unboundedMap(STRING, BBAnimationBone.CODEC), BBAnimationEntry::getBones)
		).apply(instance, BBAnimationEntry::new));

		private boolean loop;
		private int animationLength;
		private Map<String, BBAnimationBone> bones;

	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBAnimationBone {

		public static final Codec<BBAnimationBone> CODEC = create((instance) -> instance.group(
				option("rotation", BBKeyframes.getNewInstance(), BBKeyframes.CODEC, BBAnimationBone::getRotation),
				option("position", BBKeyframes.getNewInstance(), BBKeyframes.CODEC, BBAnimationBone::getPosition),
				option("scale", BBKeyframes.getNewInstance(), BBKeyframes.CODEC, BBAnimationBone::getScale)
		).apply(instance, BBAnimationBone::new));

		private BBKeyframes rotation;
		private BBKeyframes position;
		private BBKeyframes scale;

	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBKeyframes {

		public static final Codec<BBKeyframes> CODEC = Codec.unboundedMap(STRING, BBKeyframeEntry.CODEC).xmap(BBKeyframes::new, BBKeyframes::getMap);

		public BBKeyframes(Map<String, BBKeyframeEntry> map) {
			this.map = map;
		}

		private Map<String, BBKeyframeEntry> map;
		private NavigableMap<Integer, BBKeyframeEntry> compiledMap;

		public NavigableMap<Integer, BBKeyframeEntry> getCompiledMap() {
			if (this.compiledMap == null) {
				this.compiledMap = new TreeMap<>();

				for (Entry<String, BBKeyframeEntry> entry : this.map.entrySet()) {
					int ticks = (int) (Float.parseFloat(entry.getKey()) * 20);
					this.compiledMap.put(ticks, entry.getValue());
				}
			}

			return this.compiledMap;
		}

		public static Supplier<BBKeyframes> getNewInstance() {
			return () -> CodecUtils.parseNewInstanceHacky(CODEC);
		}

	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBKeyframeEntry {

		public static final Codec<BBKeyframeEntry> CODEC = create((instance) -> instance.group(
				option("vector", new Vec3f(), Vec3f.CODEC, BBKeyframeEntry::getVector),
				option("easing", "", STRING, BBKeyframeEntry::getEasing)
		).apply(instance, BBKeyframeEntry::new));

		private Vec3f vector;
		private String easing;

	}

}
