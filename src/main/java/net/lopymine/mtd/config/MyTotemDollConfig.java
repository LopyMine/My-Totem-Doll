package net.lopymine.mtd.config;

import com.google.gson.*;
import lombok.*;
import org.slf4j.*;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.sub.RenderingConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MyTotemDollConfig {

	public static final Codec<MyTotemDollConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("mod_enabled").forGetter(MyTotemDollConfig::isModEnabled),
			Codec.BOOL.fieldOf("debug_log_enabled").forGetter(MyTotemDollConfig::isDebugLogEnabled),
			RenderingConfig.CODEC.fieldOf("rendering_config").forGetter(MyTotemDollConfig::getRenderingConfig)
	).apply(instance, MyTotemDollConfig::new));

	private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(MyTotemDoll.MOD_ID + ".json5").toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LoggerFactory.getLogger(MyTotemDoll.MOD_NAME + "/Config");

	private boolean modEnabled;
	private boolean debugLogEnabled;
	private RenderingConfig renderingConfig;

	public MyTotemDollConfig() {
		this.modEnabled      = true;
		this.debugLogEnabled = false;
		this.renderingConfig = RenderingConfig.getDefault();
	}

	public MyTotemDollConfig(boolean modEnabled, boolean debugLogEnabled, RenderingConfig renderingConfig) {
		this.modEnabled      = modEnabled;
		this.debugLogEnabled = debugLogEnabled;
		this.renderingConfig = renderingConfig;
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
