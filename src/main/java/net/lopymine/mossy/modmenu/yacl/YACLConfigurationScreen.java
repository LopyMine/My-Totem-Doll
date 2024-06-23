package net.lopymine.mossy.modmenu.yacl;

import dev.isxander.yacl3.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import net.lopymine.mossy.client.MossyClient;
import net.lopymine.mossy.config.MossyConfig;
import net.lopymine.mossy.modmenu.yacl.simple.*;

import java.util.function.Function;

public class YACLConfigurationScreen {

	private static final Function<Boolean, Text> ENABLED_OR_DISABLE_FORMATTER = state -> Text.translatable("mossy.modmenu.formatter.enable_or_disable." + state);

	private YACLConfigurationScreen() {
		throw new IllegalStateException("Screen class");
	}

	public static Screen createScreen(Screen parent) {
		MossyConfig defConfig = new MossyConfig();
		MossyConfig config = MossyClient.getConfig();

		return YetAnotherConfigLib.createBuilder()
				.title(Text.translatable("mossy.modmenu.title"))
				.category(ConfigCategory.createBuilder()
						.name(Text.translatable("mossy.modmenu.title"))
						.group(getMossyGroup(defConfig, config))
						.build())
				.save(config::save)
				.build()
				.generateScreen(parent);
	}

	private static OptionGroup getMossyGroup(MossyConfig defConfig, MossyConfig config) {
		return SimpleGroupOptionBuilder.createBuilder("mossy_group").options(collector -> collector.collect(
				collector.getBooleanOption("mossy_option", defConfig.isMossy(), config::isMossy, config::setMossy, ENABLED_OR_DISABLE_FORMATTER::apply, SimpleContent.IMAGE)
		)).build();
	}
}


