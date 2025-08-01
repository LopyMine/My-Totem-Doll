package net.lopymine.mtd.config.rendering;

import java.util.function.Supplier;
import lombok.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lopymine.mtd.utils.CodecUtils;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Getter
@Setter
public class RenderingConfig {

	public static final Codec<RenderingConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("right_hand", HandRenderingConfig.getNewInstance(), HandRenderingConfig.CODEC, RenderingConfig::getRightHandConfig),
			option("left_hand", HandRenderingConfig.getNewInstance(), HandRenderingConfig.CODEC, RenderingConfig::getRightHandConfig)
	).apply(instance, RenderingConfig::new));

	private HandRenderingConfig rightHandConfig;
	private HandRenderingConfig leftHandConfig;

	public RenderingConfig(HandRenderingConfig rightHandConfig, HandRenderingConfig leftHandConfig) {
		this.rightHandConfig = rightHandConfig;
		this.leftHandConfig  = leftHandConfig;
	}

	public static Supplier<RenderingConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

}
