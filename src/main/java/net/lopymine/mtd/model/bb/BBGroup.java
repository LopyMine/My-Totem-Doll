package net.lopymine.mtd.model.bb;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import lombok.*;
import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.utils.CodecUtils;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.core.UUIDUtil;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Setter
@Getter
@AllArgsConstructor
public class BBGroup {

	public static final Codec<BBGroup> CODEC = CodecUtils.recursive("BBGroup.Codec",
			(codec) ->
					RecordCodecBuilder.create(inst -> inst.group(
							option("name", Codec.STRING, BBGroup::getName),
							option("origin", Vec3f.CODEC, BBGroup::getOrigin),
							option("rotation", new Vec3f(), Vec3f.CODEC, BBGroup::getRotation),
							option("autouv", Codec.INT, BBGroup::getAutoUV),
							option("visibility", true, Codec.BOOL, BBGroup::isVisible),
							option("uuid", UUIDUtil.AUTHLIB_CODEC, BBGroup::getUuid),
							option("children", Codec.either(codec, UUIDUtil.AUTHLIB_CODEC).listOf(), BBGroup::getChildren)
					).apply(inst, BBGroup::new))
	);

	private String name;
	private Vec3f origin;
	private Vec3f rotation;
	private int autoUV;
	private boolean visible;
	private UUID uuid;
	private List<Either<BBGroup, UUID>> children;

	public PartPose getTransformation() {
		return PartPose.offsetAndRotation(this.origin.x(), this.origin.y(), this.origin.z(), (float) -Math.toRadians(this.rotation.x()), (float) -Math.toRadians(this.rotation.y()), (float) Math.toRadians(this.rotation.z()));
	}


}
