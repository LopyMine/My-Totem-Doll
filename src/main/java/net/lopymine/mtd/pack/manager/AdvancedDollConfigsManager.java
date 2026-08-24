package net.lopymine.mtd.pack.manager;

import com.mojang.serialization.Codec;
import java.util.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.resourcepack.AdvancedDollConfig;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class AdvancedDollConfigsManager extends AbstractConfigsManager<AdvancedDollConfig> {

	private static final Map<Identifier, AdvancedDollConfig> REGISTERED_CONFIGS = new HashMap<>();

	private static final AdvancedDollConfigsManager INSTANCE = new AdvancedDollConfigsManager();

	public static AdvancedDollConfigsManager getInstance() {
		return INSTANCE;
	}

	@Override
	protected String getFolderName() {
		return "dolls/advanced";
	}

	@Override
	protected Codec<AdvancedDollConfig> getCodec() {
		return AdvancedDollConfig.CODEC;
	}

	@Override
	protected String getConfigName() {
		return "advanced doll";
	}

	@Override
	protected Logger getLogger() {
		return MyTotemDollClient.LOGGER;
	}

	@Override
	protected void registerConfig(AdvancedDollConfig config, Identifier id) {
		REGISTERED_CONFIGS.put(id, config);
	}

	@Override
	public void reload() {
		REGISTERED_CONFIGS.clear();
		super.reload();
	}

	public static Map<Identifier, AdvancedDollConfig> getRegisteredConfigs() {
		return REGISTERED_CONFIGS;
	}
}
