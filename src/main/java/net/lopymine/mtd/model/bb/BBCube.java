package net.lopymine.mtd.model.bb;

import com.google.common.collect.*;
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
import org.jetbrains.annotations.*;

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

	public BBCube(String name, Vec3f from, Vec3f to, Vec3f origin, Vec3f rotation, Dilation inflate, int autoUV, UUID uuid, boolean visible) {
		this.name     = name;
		this.from     = from;
		this.to       = to;
		this.origin   = origin;
		this.rotation = rotation;
		this.inflate  = inflate;
		this.autoUV   = autoUV;
		this.faces    = new BBCubeFaces(new HashMap<>());
		this.uuid     = uuid;
		this.visible  = visible;
	}

	public ModelTransform getTransformation() {
		return ModelTransform.of(this.origin.x(), this.origin.y(), this.origin.z(), (float) -Math.toRadians(this.rotation.x()), (float) -Math.toRadians(this.rotation.y()), (float) Math.toRadians(this.rotation.z()));
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class BBCubeFaces {

		private Map<Direction, BBCubeFace> faces;

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
