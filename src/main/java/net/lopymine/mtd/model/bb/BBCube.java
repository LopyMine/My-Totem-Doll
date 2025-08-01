package net.lopymine.mtd.model.bb;

import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.model.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Direction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.extension.DilationExtension;

import java.util.*;
import org.jetbrains.annotations.NotNull;

import static net.lopymine.mtd.utils.CodecUtils.option;


@Getter
@Setter
@AllArgsConstructor
@ExtensionMethod(DilationExtension.class)
public class BBCube {

	public static Codec<Dilation> DILATION_CODEC = Codec.FLOAT.xmap(Dilation::new, dilation -> dilation.getRadiusX());

	public static final Codec<BBCube> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			option("name", Codec.STRING, BBCube::getName),
			option("from", Vec3f.CODEC, BBCube::getFrom),
			option("to", Vec3f.CODEC, BBCube::getTo),
			option("origin", Vec3f.CODEC, BBCube::getOrigin),
			option("rotation", new Vec3f(), Vec3f.CODEC, BBCube::getRotation),
			option("inflate", Dilation.NONE, DILATION_CODEC, BBCube::getInflate),
			option("autouv", Codec.INT, BBCube::getAutoUV),
			option("faces", BBCubeFaces.CODEC, BBCube::getFaces),
			option("uuid", Uuids.CODEC, BBCube::getUuid),
			option("visibility", true, Codec.BOOL, BBCube::isVisible)
	).apply(inst, BBCube::new));

	private String name;
	private Vec3f from;
	private Vec3f to;
	private Vec3f origin;
	private Vec3f rotation;
	private Dilation inflate;
	private int autoUV;
	private BBCubeFaces faces;
	private UUID uuid;
	private boolean visible;

	public ModelTransform getTransformation() {
		return ModelTransform.of(this.origin.x(), this.origin.y(), this.origin.z(), (float) -Math.toRadians(this.rotation.x()), (float) -Math.toRadians(this.rotation.y()), (float) Math.toRadians(this.rotation.z()));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBCubeFaces {

		public static final Codec<BBCubeFaces> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				option("up", BBCubeFace.CODEC, BBCubeFaces::getUp),
				option("down", BBCubeFace.CODEC, BBCubeFaces::getDown),
				option("north", BBCubeFace.CODEC, BBCubeFaces::getNorth),
				option("south", BBCubeFace.CODEC, BBCubeFaces::getSouth),
				option("east", BBCubeFace.CODEC, BBCubeFaces::getEast),
				option("west", BBCubeFace.CODEC, BBCubeFaces::getWest)
		).apply(inst, BBCubeFaces::new));

		private BBCubeFace up;
		private BBCubeFace down;
		private BBCubeFace north;
		private BBCubeFace south;
		private BBCubeFace east;
		private BBCubeFace west;

		@NotNull
		public Map<Direction, BBCubeFace> map() {
			return Map.ofEntries(
					Map.entry(Direction.UP, this.up),
					Map.entry(Direction.DOWN, this.down),
					Map.entry(Direction.EAST, this.east),
					Map.entry(Direction.WEST, this.west),
					Map.entry(Direction.SOUTH, this.south),
					Map.entry(Direction.NORTH, this.north)
			);
		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBCubeFace {

		public static final Codec<BBCubeFace> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				option("uv", UV.CODEC, BBCubeFace::getUv),
				option("rotation",0, Codec.INT, BBCubeFace::getRotation)
		).apply(inst, BBCubeFace::new));

		private UV uv;
		private int rotation;

	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class UV {

		public static final Codec<UV> CODEC = Codec.FLOAT.listOf()
				.comapFlatMap(
						(coordinates) -> Util.decodeFixedLengthList(coordinates, 4)
								.map((list) -> new UV(list.get(0), list.get(1), list.get(2), list.get(3))),
						(vec) -> List.of(vec.getFromU(), vec.getFromV(), vec.getToU(), vec.getToV())
				);

		private float fromU;
		private float fromV;
		private float toU;
		private float toV;

		public boolean isDummy() {
			return this.fromU == this.toU && this.fromV == this.toV;
		}
	}
}
