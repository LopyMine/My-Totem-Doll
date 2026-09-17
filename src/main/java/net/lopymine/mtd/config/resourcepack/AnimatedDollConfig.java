package net.lopymine.mtd.config.resourcepack;

import com.mojang.serialization.Codec;
import java.util.*;
import java.util.function.Supplier;
import lombok.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.model.bb.manager.BlockBenchAnimationsManager;
import net.lopymine.mtd.utils.CodecUtils;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;
import static com.mojang.serialization.Codec.STRING;
import static com.mojang.serialization.codecs.RecordCodecBuilder.create;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Setter
@Getter
@SuppressWarnings("unused")
public class AnimatedDollConfig {

	public static final Set<String> REGISTERED_DEFINITIONS = new HashSet<>();

	public static final String MODELS_FOLDER = "dolls/";
	public static final String ANIMATIONS_FOLDER = "dolls/animations/";
	public static final String[] ANIMATIONS_EXTENSIONS = {".json", ".json5"};

	public static final Codec<AnimatedDollConfig> CODEC = create((instance) -> instance.group(
			option("standard_animation", "", STRING, AnimatedDollConfig::getStandardAnimation),
			option("standard_model", "", STRING, AnimatedDollConfig::getStandardModel),
			option("definition", DefinitionCollection.getNewInstance(), DefinitionCollection.CODEC, AnimatedDollConfig::getDefinition)
	).apply(instance, AnimatedDollConfig::new));

	static {
		for (DollRenderContext value : DollRenderContext.values()) {
			REGISTERED_DEFINITIONS.add(getDefinitionName(value));
		}
	}

	private String standardAnimation;
	private String standardModel;
	private DefinitionCollection definition;
	@Nullable
	private Identifier currentConfigId;
	@Nullable
	private AnimationReference standardAnimationReference;
	@Nullable
	private Identifier standardModelId;

	public AnimatedDollConfig(String standardAnimation, String standardModel, DefinitionCollection definition) {
		this.standardAnimation = standardAnimation;
		this.standardModel     = standardModel;
		this.definition        = definition;
	}

	public static Supplier<AnimatedDollConfig> getNewInstance() {
		return () -> CodecUtils.parseNewInstanceHacky(CODEC);
	}

	public static String getDefinitionName(@NotNull DollRenderContext context) {
		return context.name().toUpperCase(Locale.ROOT).substring(2);
	}

	@Nullable
	private static AnimationReference resolveAnimation(@NotNull Identifier configId, @Nullable String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		int index = value.lastIndexOf('/');
		if (index <= 0 || index + 1 >= value.length()) {
			MyTotemDollClient.LOGGER.warn("Failed to parse animation \"{}\" of animated doll config \"{}\", expected \"animations_file/ANIMATION_NAME\"", value, configId);
			return null;
		}

		String file = value.substring(0, index);
		String name = value.substring(index + 1);

		Identifier fileId = findAnimationsFileId(configId, file);
		if (fileId == null) {
			MyTotemDollClient.LOGGER.warn("Failed to find animations file \"{}\" of animated doll config \"{}\"", file, configId);
			return null;
		}

		return new AnimationReference(fileId, name, fileId + "/" + name);
	}

	@Nullable
	private static Identifier findAnimationsFileId(@NotNull Identifier configId, @NotNull String file) {
		for (String extension : ANIMATIONS_EXTENSIONS) {
			if (!file.endsWith(extension)) {
				continue;
			}
			return resolveId(configId, ANIMATIONS_FOLDER, file);
		}

		for (String extension : ANIMATIONS_EXTENSIONS) {
			Identifier id = resolveId(configId, ANIMATIONS_FOLDER, file + extension);
			if (id != null && BlockBenchAnimationsManager.getRegisteredConfigs().containsKey(id)) {
				return id;
			}
		}

		return null;
	}

	@Nullable
	private static Identifier resolveId(@NotNull Identifier configId, String folder, @Nullable String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		String namespace = configId.getNamespace();
		String path = value;

		int index = value.indexOf(':');
		if (index != -1) {
			namespace = value.substring(0, index);
			path      = value.substring(index + 1);
		}

		return Identifier.tryBuild(namespace, folder + path);
	}

	public void bind(@NotNull Identifier configId) {
		this.currentConfigId            = configId;
		this.standardAnimationReference = resolveAnimation(configId, this.standardAnimation);
		this.standardModelId            = resolveId(configId, MODELS_FOLDER, this.standardModel);

		for (Map.Entry<String, Definition> entry : this.definition.getMap().entrySet()) {
			String definitionName = entry.getKey();
			Definition value = entry.getValue();

			if (!REGISTERED_DEFINITIONS.contains(definitionName)) {
				MyTotemDollClient.LOGGER.warn("Found unknown definition \"{}\" in animated doll config \"{}\"", definitionName, configId);
			}

			value.setAnimationReference(resolveAnimation(configId, value.getAnimation()));
			value.setModelId(resolveId(configId, MODELS_FOLDER, value.getModel()));
		}
	}

	@Nullable
	public Definition getDefinition(@NotNull DollRenderContext context) {
		return this.definition.getMap().get(getDefinitionName(context));
	}

	@Nullable
	public AnimationReference getAnimationReference(@NotNull DollRenderContext context) {
		Definition definition = this.getDefinition(context);

		if (definition != null) {
			if (definition.isAnimationDisabled()) {
				return null;
			}
			if (definition.getAnimationReference() != null) {
				return definition.getAnimationReference();
			}
		}
		return this.standardAnimationReference;
	}

	@Nullable
	public Identifier getModelId(@NotNull DollRenderContext context) {
		Definition definition = this.getDefinition(context);
		if (definition != null && definition.getModelId() != null) {
			return definition.getModelId();
		}
		return this.standardModelId;
	}

	public record AnimationReference(@NotNull Identifier fileId, @NotNull String name, @NotNull String bakeKey) {

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
	public static class Definition {

		public static final Codec<Definition> CODEC = create((instance) -> instance.group(
				option("animation", (String) null, STRING, Definition::getAnimation),
				option("model", (String) null, STRING, Definition::getModel)
		).apply(instance, Definition::new));

		@Nullable
		private String animation;
		@Nullable
		private String model;

		@Nullable
		private AnimationReference animationReference;
		@Nullable
		private Identifier modelId;

		public Definition(@Nullable String animation, @Nullable String model) {
			this.animation = animation;
			this.model     = model;
		}

		public boolean isAnimationDisabled() {
			return this.animation != null && this.animation.isBlank();
		}

		public static Supplier<Definition> getNewInstance() {
			return () -> CodecUtils.parseNewInstanceHacky(CODEC);
		}

	}

}
