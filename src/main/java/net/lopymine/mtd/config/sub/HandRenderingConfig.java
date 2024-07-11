package net.lopymine.mtd.config.sub;

import lombok.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@Getter
@Setter
public class HandRenderingConfig {

	public static final Codec<HandRenderingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.fieldOf("scale").forGetter(HandRenderingConfig::getScale),
			Codec.DOUBLE.fieldOf("offsetX").forGetter(HandRenderingConfig::getOffsetX),
			Codec.DOUBLE.fieldOf("offsetY").forGetter(HandRenderingConfig::getOffsetY),
			Codec.DOUBLE.fieldOf("offsetZ").forGetter(HandRenderingConfig::getOffsetZ),
			Codec.DOUBLE.fieldOf("rotationX").forGetter(HandRenderingConfig::getRotationX),
			Codec.DOUBLE.fieldOf("rotationY").forGetter(HandRenderingConfig::getRotationY),
			Codec.DOUBLE.fieldOf("rotationZ").forGetter(HandRenderingConfig::getRotationZ)
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

	public static HandRenderingConfig getDefault() {
		return new HandRenderingConfig(1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
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
