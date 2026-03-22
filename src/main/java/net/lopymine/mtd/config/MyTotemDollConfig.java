package net.lopymine.mtd.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.*;
import net.fabricmc.loader.api.FabricLoader;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.other.vector.Vec2i;
import net.lopymine.mtd.config.rendering.RenderingConfig;
import net.lopymine.mtd.config.totem.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.utils.*;
import net.minecraft.resources.Identifier;
import org.slf4j.*;
import static net.lopymine.mtd.utils.CodecUtils.option;

@Getter
@Setter
@AllArgsConstructor
public class MyTotemDollConfig {

	public static final Codec<MyTotemDollConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("mod_enabled", true, Codec.BOOL, MyTotemDollConfig::isModEnabled),
			option("debug_log_enabled", false, Codec.BOOL, MyTotemDollConfig::isDebugLogEnabled),
			option("rendering_config", RenderingConfig.getNewInstance(), RenderingConfig.CODEC, MyTotemDollConfig::getRenderingConfig),
			option("standard_doll_skin_data", "", Codec.STRING, MyTotemDollConfig::getStandardTotemDollSkinValue),
			option("standard_doll_skin_type", TotemDollSkinType.STEVE, TotemDollSkinType.CODEC, MyTotemDollConfig::getStandardTotemDollSkinType),
			option("selected_standard_doll_model_data", TotemDollModel.NONE, Identifier.CODEC, MyTotemDollConfig::getSelectedStandardTotemDollModelValue),
			option("standard_doll_model_data", TotemDollModel.TWO_D_MODEL_ID, Identifier.CODEC, MyTotemDollConfig::getStandardTotemDollModelValue),
			option("standard_doll_model_arms_type", TotemDollArmsType.WIDE, TotemDollArmsType.CODEC, MyTotemDollConfig::getStandardTotemDollArmsType),
			option("tag_button_pos", new Vec2i(155, 48), Vec2i.CODEC, MyTotemDollConfig::getTagButtonPos),
			option("use_vanilla_totem_model", false, Codec.BOOL, MyTotemDollConfig::isUseVanillaTotemModel),
			Codec.INT.optionalFieldOf("better_tag_menu_tooltip_size")
					.xmap(o -> o.orElse(60), Optional::of)
					.forGetter(MyTotemDollConfig::getBetterTagMenuTooltipSize),
			option("tag_menu_tooltip_model_scale", 1.0F, Codec.FLOAT, MyTotemDollConfig::getTagMenuTooltipModelScale),
			option("executor_threads_count", 6, Codec.INT, MyTotemDollConfig::getParallelTasksCount),
			option("first_run", true, Codec.BOOL, MyTotemDollConfig::isFirstRun),
			option("first_run_temp", true, Codec.BOOL, MyTotemDollConfig::isFirstRunTemp),
			option("support_other_mods_totems", true, Codec.BOOL, MyTotemDollConfig::isSupportOtherModsTotems)
	).apply(instance, MyTotemDollConfig::new));

	private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(MyTotemDoll.MOD_ID + ".json5").toFile();
	private static final Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Config");
	private static MyTotemDollConfig INSTANCE;

	private boolean modEnabled;
	private boolean debugLogEnabled;
	private RenderingConfig renderingConfig;
	private String standardTotemDollSkinValue;
	private TotemDollSkinType standardTotemDollSkinType;
	private Identifier selectedStandardTotemDollModelValue;
	private Identifier standardTotemDollModelValue;
	private TotemDollArmsType standardTotemDollArmsType;
	private Vec2i tagButtonPos;
	private boolean useVanillaTotemModel;
	private int betterTagMenuTooltipSize;
	private float tagMenuTooltipModelScale;
	private int parallelTasksCount;
	private boolean firstRun;
	private boolean firstRunTemp;
	private boolean supportOtherModsTotems;

	public Identifier getSelectedStandardTotemDollModelValue() {
		return this.selectedStandardTotemDollModelValue == TotemDollModel.NONE ? this.selectedStandardTotemDollModelValue = this.standardTotemDollModelValue : this.selectedStandardTotemDollModelValue;
	}

	private MyTotemDollConfig() {
		throw new IllegalArgumentException();
	}

	public static MyTotemDollConfig getInstance() {
		return INSTANCE == null ? reload() : INSTANCE;
	}

	public static MyTotemDollConfig reload() {
		return INSTANCE = MyTotemDollConfig.read();
	}

	public static MyTotemDollConfig getNewInstance() {
		return CodecUtils.parseNewInstanceHacky(CODEC);
	}

	private static MyTotemDollConfig read() {
		return ConfigUtils.readConfig(CODEC, CONFIG_FILE, LOGGER);
	}

	public void saveAsync() {
		CompletableFuture.runAsync(this::save);
	}

	public void save() {
		ConfigUtils.saveConfig(this, CODEC, CONFIG_FILE, LOGGER);
	}
}
