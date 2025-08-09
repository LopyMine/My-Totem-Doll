package net.lopymine.mtd.config;

import com.google.gson.*;
import lombok.*;


import net.minecraft.util.Identifier;
import org.slf4j.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.rendering.*;
import net.lopymine.mtd.config.totem.*;
import net.lopymine.mtd.config.other.vector.Vec2i;
import net.lopymine.mtd.doll.model.TotemDollModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

import static net.lopymine.mtd.utils.CodecUtils.option;
import static net.lopymine.mtd.utils.CodecUtils.optional;

@Getter
@Setter
@AllArgsConstructor
public class MyTotemDollConfig {

	public static final Codec<MyTotemDollConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			option("mod_enabled", Codec.BOOL, MyTotemDollConfig::isModEnabled),
			option("debug_log_enabled", Codec.BOOL, MyTotemDollConfig::isDebugLogEnabled),
			option("rendering_config", RenderingConfig.CODEC, MyTotemDollConfig::getRenderingConfig),
			optional("standard_doll_skin_data", "", Codec.STRING, MyTotemDollConfig::getStandardTotemDollSkinValue),
			optional("standard_doll_skin_type", TotemDollSkinType.STEVE, TotemDollSkinType.CODEC, MyTotemDollConfig::getStandardTotemDollSkinType),
			optional("standard_doll_model_data", TotemDollModel.TWO_D_MODEL_ID, Identifier.CODEC, MyTotemDollConfig::getStandardTotemDollModelValue),
			optional("standard_doll_model_arms_type", TotemDollArmsType.WIDE, TotemDollArmsType.CODEC, MyTotemDollConfig::getStandardTotemDollArmsType),
			optional("tag_button_pos", new Vec2i(155, 48), Vec2i.CODEC, MyTotemDollConfig::getTagButtonPos),
			optional("use_vanilla_totem_model", false, Codec.BOOL, MyTotemDollConfig::isUseVanillaTotemModel),

			Codec.INT.optionalFieldOf("better_tag_menu_tooltip_size")
					.xmap(o -> o.orElse(60), Optional::of)
					.forGetter(MyTotemDollConfig::getBetterTagMenuTooltipSize),

			optional("tag_menu_tooltip_model_scale", 1.0F, Codec.FLOAT, MyTotemDollConfig::getTagMenuTooltipModelScale),
			optional("executor_threads_count", 6, Codec.INT, MyTotemDollConfig::getParallelTasksCount),
			optional("first_run", true, Codec.BOOL, MyTotemDollConfig::isFirstRun),
			optional("support_other_mods_totems", true, Codec.BOOL, MyTotemDollConfig::isSupportOtherModsTotems)
	).apply(instance, MyTotemDollConfig::new));

	private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(MyTotemDoll.MOD_ID + ".json5").toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Config");

	private boolean modEnabled;
	private boolean debugLogEnabled;
	private RenderingConfig renderingConfig;
	private String standardTotemDollSkinValue;
	private TotemDollSkinType standardTotemDollSkinType;
	private Identifier standardTotemDollModelValue;
	private TotemDollArmsType standardTotemDollArmsType;
	private Vec2i tagButtonPos;
	private boolean useVanillaTotemModel;
	private int betterTagMenuTooltipSize;
	private float tagMenuTooltipModelScale;
	private int parallelTasksCount;
	private boolean firstRun;
	private boolean supportOtherModsTotems;

	public MyTotemDollConfig() {
		this.modEnabled                  = true;
		this.debugLogEnabled             = false;
		this.renderingConfig             = RenderingConfig.getDefault();
		this.standardTotemDollSkinValue  = "";
		this.standardTotemDollSkinType   = TotemDollSkinType.STEVE;
		this.standardTotemDollModelValue = TotemDollModel.TWO_D_MODEL_ID;
		this.standardTotemDollArmsType   = TotemDollArmsType.WIDE;
		this.tagButtonPos                = new Vec2i(155, 48);
		this.useVanillaTotemModel        = false;
		this.betterTagMenuTooltipSize    = 60;
		this.tagMenuTooltipModelScale    = 1.0F;
		this.parallelTasksCount          = 6;
		this.firstRun                    = true;
		this.supportOtherModsTotems      = true;
	}

	public static MyTotemDollConfig getInstance() {
		return MyTotemDollConfig.read();
	}

	private static @NotNull MyTotemDollConfig create() {
		MyTotemDollConfig config = new MyTotemDollConfig();
		try (FileWriter writer = new FileWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
			String json = GSON.toJson(CODEC.encode(config, JsonOps.INSTANCE, JsonOps.INSTANCE.empty())/*? if >=1.20.5 {*/.getOrThrow());/*?} else*//*.getOrThrow(false, LOGGER::error));*/
			writer.write(json);
		} catch (Exception e) {
			LOGGER.error("Failed to create config", e);
		}
		return config;
	}

	private static MyTotemDollConfig read() {
		if (!CONFIG_FILE.exists()) {
			return MyTotemDollConfig.create();
		}

		try (FileReader reader = new FileReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
			return CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(reader))/*? if >=1.20.5 {*/.getOrThrow()/*?} else {*//*.getOrThrow(false, LOGGER::error)*//*?}*/.getFirst();
		} catch (Exception e) {
			LOGGER.error("Failed to read config", e);
		}
		return MyTotemDollConfig.create();
	}

	public void save() {
		MyTotemDollClient.setConfig(this);
		CompletableFuture.runAsync(() -> {
			try (FileWriter writer = new FileWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
				String json = GSON.toJson(CODEC.encode(this, JsonOps.INSTANCE, JsonOps.INSTANCE.empty())/*? if >=1.20.5 {*/.getOrThrow());/*?} else*//*.getOrThrow(false, LOGGER::error));*/
				writer.write(json);
			} catch (Exception e) {
				LOGGER.error("Failed to save config", e);
			}
		});
	}
}
