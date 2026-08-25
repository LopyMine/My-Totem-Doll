package net.lopymine.mtd.pack.manager;

import com.mojang.serialization.Codec;
import java.util.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.resourcepack.AnimatedDollConfig;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AnimatedDollConfigsManager extends AbstractConfigsManager<AnimatedDollConfig> {

	private static final Map<Identifier, AnimatedDollConfig> REGISTERED_CONFIGS = new HashMap<>();
	private static final Map<Identifier, AnimatedDollConfig> CONFIGS_BY_MODEL = new HashMap<>();

	private static final AnimatedDollConfigsManager INSTANCE = new AnimatedDollConfigsManager();

	public static AnimatedDollConfigsManager getInstance() {
		return INSTANCE;
	}

	public static Map<Identifier, AnimatedDollConfig> getRegisteredConfigs() {
		return REGISTERED_CONFIGS;
	}

	@Nullable
	public static AnimatedDollConfig getConfigByModelId(@Nullable Identifier modelId) {
		return modelId == null ? null : CONFIGS_BY_MODEL.get(modelId);
	}

	@Override
	protected String getFolderName() {
		return "dolls/animated";
	}

	@Override
	protected Logger getLogger() {
		return MyTotemDollClient.LOGGER;
	}

	@Override
	protected Codec<AnimatedDollConfig> getCodec() {
		return AnimatedDollConfig.CODEC;
	}

	@Override
	protected String getConfigName() {
		return "animated doll";
	}

	@Override
	protected void registerConfig(AnimatedDollConfig config, Identifier id) {
		config.bind(id);
		REGISTERED_CONFIGS.put(id, config);

		Identifier modelId = config.getStandardModelId();
		if (modelId != null) {
			CONFIGS_BY_MODEL.put(modelId, config);
		}
	}

	@Override
	public void reload() {
		REGISTERED_CONFIGS.clear();
		CONFIGS_BY_MODEL.clear();
		super.reload();
	}
}
