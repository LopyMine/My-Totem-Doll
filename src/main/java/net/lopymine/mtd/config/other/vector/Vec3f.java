package net.lopymine.mtd.config.other.vector;

import com.mojang.serialization.Codec;
import java.util.List;
import lombok.*;
import net.minecraft.util.Util;
import org.joml.*;

@Getter
@Setter
@AllArgsConstructor
public class Vec3f extends Vector3f {

	public static final Codec<Vec3f> CODEC = Codec.FLOAT.listOf()
			.comapFlatMap(
					(coordinates) -> Util.fixedSize(coordinates, 3)
							.map((list) -> new Vec3f(list.get(0), list.get(1), list.get(2))),
					(vec) -> List.of(vec.x(), vec.y(), vec.z())
			);

	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3f(Vector3fc vector3f) {
		this.x = vector3f.x();
		this.y = vector3f.y();
		this.z = vector3f.z();
	}

	public Vec3f copy() {
		return new Vec3f(this.x(), this.y(), this.z());
	}
}
