package net.lopymine.mtd.config.other.vector;

import lombok.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import static net.lopymine.mtd.utils.CodecUtils.option;

@Setter
@Getter
@AllArgsConstructor
public class Vec2i {

	public static final Codec<Vec2i> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("x", Codec.INT, Vec2i::getX),
			option("y", Codec.INT, Vec2i::getY)
	).apply(instance, Vec2i::new));
	private int x;
	private int y;

	public Vec2i() {
		this.x = 0;
		this.y = 0;
	}
}
