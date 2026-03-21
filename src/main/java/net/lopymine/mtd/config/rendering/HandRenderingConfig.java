package net.lopymine.mtd.config.rendering;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.mtd.utils.CodecUtils;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Getter
@Setter
public class HandRenderingConfig {

	public static final Codec<HandRenderingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("scale", 1.0D, Codec.DOUBLE, HandRenderingConfig::getScale),
			option("offsetX", 0.0D, Codec.DOUBLE, HandRenderingConfig::getOffsetX),
			option("offsetY", 0.0D, Codec.DOUBLE, HandRenderingConfig::getOffsetY),
			option("offsetZ", 0.0D, Codec.DOUBLE, HandRenderingConfig::getOffsetZ),
			option("rotationX", 0.0D, Codec.DOUBLE, HandRenderingConfig::getRotationX),
			option("rotationY", 0.0D, Codec.DOUBLE, HandRenderingConfig::getRotationY),
			option("rotationZ", 0.0D, Codec.DOUBLE, HandRenderingConfig::getRotationZ)
	).apply(instance, HandRenderingConfig::new));

	private double scale;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private double rotationX;
	private double rotationY;
	private double rotationZ;

	public HandRenderingConfig(double scale, double offsetX, double offsetY, double offsetZ, double rotationX, double rotationY, double rotationZ) {
		this.scale     = scale;
		this.offsetX   = offsetX;
		this.offsetY   = offsetY;
		this.offsetZ   = offsetZ;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
	}

	public static Supplier<HandRenderingConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public void copy(HandRenderingConfig anotherHandConfig) {
		this.scale     = anotherHandConfig.getScale();
		this.offsetX   = anotherHandConfig.getOffsetX();
		this.offsetY   = anotherHandConfig.getOffsetY();
		this.offsetZ   = anotherHandConfig.getOffsetZ();
		this.rotationX = anotherHandConfig.getRotationX();
		this.rotationY = anotherHandConfig.getRotationY();
		this.rotationZ = anotherHandConfig.getRotationZ();
	}
}
