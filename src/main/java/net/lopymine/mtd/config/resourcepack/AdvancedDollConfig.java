package net.lopymine.mtd.config.resourcepack;

import com.google.gson.*;
import com.mojang.serialization.*;
import java.util.*;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.mtd.config.rendering.HandRenderingConfig;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.utils.CodecUtils;
import net.lopymine.mtd.utils.CodecUtils.*;
import static net.lopymine.mtd.utils.CodecUtils.option;
import static com.mojang.serialization.Codec.STRING;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;;

@Setter
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public class AdvancedDollConfig {

	public static final Set<String> REGISTERED_DEFINITIONS = new HashSet<>();

	public static final Codec<AdvancedDollConfig> CODEC = create((instance) -> instance.group(
			option("animation_file", "", STRING, AdvancedDollConfig::getAnimationFile),
			option("standard_model", "", STRING, AdvancedDollConfig::getStandardModel),
			option("definition", DefinitionCollection.getNewInstance(), DefinitionCollection.CODEC, AdvancedDollConfig::getDefinition)
	).apply(instance, AdvancedDollConfig::new));

	private String animationFile;
	private String standardModel;
	private DefinitionCollection definition;

	public static Supplier<AdvancedDollConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	@Setter
	@Getter
	@AllArgsConstructor
	public static class DefinitionCollection {

		public static Codec<DefinitionCollection> CODEC = Codec.unboundedMap(STRING, Definition.CODEC).xmap(DefinitionCollection::new, DefinitionCollection::getMap);

		private Map<String, Definition> map;

		public static Supplier<DefinitionCollection> getNewInstance() {
			return () -> CodecUtils.parseNewInstanceHacky(CODEC);
		}

	}

	@Setter
	@Getter
	@AllArgsConstructor
	public static class Definition {

		public static final Codec<Definition> CODEC = create((instance) -> instance.group(
				option("animation_name", "", STRING, Definition::getAnimationName),
				option("override_model", "", STRING, Definition::getOverrideModel)
		).apply(instance, Definition::new));

		private String animationName;
		private String overrideModel;

		public static Supplier<Definition> getNewInstance() {
			return () -> CodecUtils.parseNewInstanceHacky(CODEC);
		}

	}

	static {
		for (DollRenderContext value : DollRenderContext.values()) {
			REGISTERED_DEFINITIONS.add(value.name().toUpperCase(Locale.ROOT).substring(2));
		}
	}

}
