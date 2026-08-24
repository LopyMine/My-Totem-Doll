package net.lopymine.mtd.model.bb.manager;

import com.mojang.serialization.Codec;
import java.util.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.resourcepack.AdvancedDollConfig;
import net.lopymine.mtd.model.bb.BBAnimation;
import net.lopymine.mtd.pack.manager.AbstractConfigsManager;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class BlockBenchAnimationsManager extends AbstractConfigsManager<BBAnimation> {

	private static final Map<Identifier, BBAnimation> REGISTERED_CONFIGS = new HashMap<>();

	private static final BlockBenchAnimationsManager INSTANCE = new BlockBenchAnimationsManager();

	public static BlockBenchAnimationsManager getInstance() {
		return INSTANCE;
	}

	@Override
	protected String getFolderName() {
		return "dolls/animations";
	}

	@Override
	protected Codec<BBAnimation> getCodec() {
		return BBAnimation.CODEC;
	}

	@Override
	protected String getConfigName() {
		return "doll animation";
	}

	@Override
	protected Logger getLogger() {
		return MyTotemDollClient.LOGGER;
	}

	@Override
	protected void registerConfig(BBAnimation config, Identifier id) {
		REGISTERED_CONFIGS.put(id, config);
	}

	@Override
	public void reload() {
		REGISTERED_CONFIGS.clear();
		super.reload();
	}

	public static Map<Identifier, BBAnimation> getRegisteredConfigs() {
		return REGISTERED_CONFIGS;
	}
}
