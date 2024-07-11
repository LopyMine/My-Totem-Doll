package net.lopymine.mtd.config.sub;

import lombok.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

@Getter
@Setter
public class RenderingConfig {

	public static final Codec<RenderingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			HandRenderingConfig.CODEC.fieldOf("right_hand").forGetter(RenderingConfig::getRightHandConfig),
			HandRenderingConfig.CODEC.fieldOf("left_hand").forGetter(RenderingConfig::getLeftHandConfig)
	).apply(instance, RenderingConfig::new));

	private HandRenderingConfig rightHandConfig;
	private HandRenderingConfig leftHandConfig;

	public RenderingConfig(HandRenderingConfig rightHandConfig, HandRenderingConfig leftHandConfig) {
		this.rightHandConfig = rightHandConfig;
		this.leftHandConfig  = leftHandConfig;
	}

	public static RenderingConfig getDefault() {
		return new RenderingConfig(HandRenderingConfig.getDefault(), HandRenderingConfig.getDefault());
	}
}
