package net.lopymine.mtd.model.bb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.extension.ModelTransformationExtension;
import net.minecraft.client.resources.model.cuboid.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import static net.lopymine.mtd.utils.CodecUtils.option;


@Setter
@Getter
@AllArgsConstructor
public class BBModel {

	private Identifier location;
	private String name;
	private BBModelMeta meta;
	private BBModelResolution resolution;
	private List<BBCube> cubes;
	private List<BBGroup> groups;
	private boolean frontGuiLight;
	private ItemTransforms transformation;

	@Nullable
	public BBCube getCube(UUID uuid) {
		for (BBCube cube : this.cubes) {
			if (cube.getUuid().equals(uuid)) {
				return cube;
			}
		}
		return null;
	}

	@Setter
	@Getter
	@AllArgsConstructor
	public static class BBModelMeta {

		public static final Codec<BBModelMeta> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				option("format_version", Codec.STRING, BBModelMeta::getVersion),
				option("model_format", Codec.STRING, BBModelMeta::getModel)
		).apply(inst, BBModelMeta::new));

		private String version;
		private String model;

	}

	@Setter
	@Getter
	@AllArgsConstructor
	public static class BBModelResolution {

		public static final Codec<BBModelResolution> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				option("width", Codec.INT, BBModelResolution::getWidth),
				option("height", Codec.INT, BBModelResolution::getHeight)
		).apply(inst, BBModelResolution::new));

		private int width;
		private int height;

	}

	@ExtensionMethod(ModelTransformationExtension.class)
	public static final class Transformations {

		private static final Vec3f DEFAULT_ROTATION = new Vec3f(0.0F, 0.0F, 0.0F);
		private static final Vec3f DEFAULT_TRANSLATION = new Vec3f(0.0F, 0.0F, 0.0F);
		private static final Vec3f DEFAULT_SCALE = new Vec3f(1.0F, 1.0F, 1.0F);

		public static final Codec<ItemTransform> TRANSFORMATION_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				option("rotation", DEFAULT_ROTATION, Vec3f.CODEC, (o) -> new Vec3f(o.rotation())),
				option("translation", DEFAULT_TRANSLATION, Vec3f.CODEC, (o) -> new Vec3f(o.translation())),
				option("scale", DEFAULT_SCALE, Vec3f.CODEC, (o) -> new Vec3f(o.scale()))
		).apply(instance, Transformations::prepareTransformation));

		public static final Codec<ItemTransforms> MODEL_TRANSFORMATION_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
				option(DollRenderContext.D_THIRD_PERSON_LEFT_HAND.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getTl()),
				option(DollRenderContext.D_THIRD_PERSON_RIGHT_HAND.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getTr()),
				option(DollRenderContext.D_FIRST_PERSON_LEFT_HAND.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getFl()),
				option(DollRenderContext.D_FIRST_PERSON_RIGHT_HAND.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getFr()),
				option(DollRenderContext.D_HEAD.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getHead()),
				option(DollRenderContext.D_GUI.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getGui()),
				option(DollRenderContext.D_GROUND.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getGround()),
				option(DollRenderContext.D_FIXED.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getFixed()),
				option(DollRenderContext.D_ON_SHELF.getId(), ItemTransform.NO_TRANSFORM, TRANSFORMATION_CODEC, (o) -> o.getOnShelf())

		).apply(instance, ItemTransforms::new));

		private static ItemTransform prepareTransformation(Vec3f rotation, Vec3f translation, Vec3f scale) {
			translation.mul(0.0625F);
			return new ItemTransform(rotation, translation, scale);
		}

	}
}
