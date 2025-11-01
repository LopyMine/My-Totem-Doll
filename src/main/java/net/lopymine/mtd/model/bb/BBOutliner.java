package net.lopymine.mtd.model.bb;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import lombok.*;
import net.lopymine.mtd.utils.CodecUtils;
import net.minecraft.util.Uuids;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Setter
@Getter
@AllArgsConstructor
public class BBOutliner {

	public static final Codec<BBOutliner> CODEC = CodecUtils.recursive("BBOutliner.Codec",
			(codec) ->
					RecordCodecBuilder.create(inst -> inst.group(
							option("uuid", Uuids.CODEC, BBOutliner::getUuid),
							option("children", Codec.either(codec, Uuids.CODEC).listOf(), BBOutliner::getChildren)
					).apply(inst, BBOutliner::new))
	);

	private UUID uuid;
	private List<Either<BBOutliner, UUID>> children;

}
