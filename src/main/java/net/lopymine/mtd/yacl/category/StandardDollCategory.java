package net.lopymine.mtd.yacl.category;

import dev.isxander.yacl3.api.*;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.totem.*;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.TotemDollManager;
import net.lopymine.mtd.extension.SimpleOptionExtension;
import net.lopymine.mtd.yacl.custom.controller.totem.TotemDollModelControllerBuilder;
import net.lopymine.mtd.yacl.custom.renderer.TotemDollPreviewRenderer;
import net.lopymine.mtd.yacl.custom.simple.main.*;
import net.minecraft.resources.Identifier;

@ExtensionMethod(SimpleOptionExtension.class)
public class StandardDollCategory {

	public static ConfigCategory get(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		TotemDollPreviewRenderer renderer = new TotemDollPreviewRenderer();

		List<Option<?>> options = new ArrayList<>();

		Option<Boolean> useVanillaTotemModelOption = SimpleOption.<Boolean>startBuilder("use_vanilla_totem_model")
				.withCustomDescription(renderer)
				.withBinding(defConfig.isUseVanillaTotemModel(), config::isUseVanillaTotemModel, (value) -> {
					config.setUseVanillaTotemModel(value);
					for (Option<?> option : options) {
						option.setAvailable(!value);
					}
				}, true)
				.withController()
				.build();

		ConfigCategory standardDollCategory = SimpleCategory.startBuilder("standard_doll")
				.options(useVanillaTotemModelOption)
				.groups(getStandardDollSkinGroup(defConfig, config, renderer))
				.groups(getStandardDollModelGroup(defConfig, config, renderer))
				.build();

		for (OptionGroup group : standardDollCategory.groups()) {
			for (Option<?> option : group.options()) {
				if (option == useVanillaTotemModelOption) {
					continue;
				}
				option.setAvailable(!useVanillaTotemModelOption.pendingValue());
				options.add(option);
			}
		}

		return standardDollCategory;
	}

	private static OptionGroup getStandardDollSkinGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config, TotemDollPreviewRenderer renderer) {
		Option<String> standardDollSkinDataOption = SimpleOption.<String>startBuilder("standard_doll_skin_data")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardTotemDollSkinValue(), config::getStandardTotemDollSkinValue, (value) -> {
					config.setStandardTotemDollSkinValue(value);
					renderer.updateDoll();
				}, true)
				.withController()
				.build();

		standardDollSkinDataOption.setAvailable(config.getStandardTotemDollSkinType().isNeedData());

		Option<TotemDollSkinType> standardDollSkinTypeOption = SimpleOption.<TotemDollSkinType>startBuilder("standard_doll_skin_type")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardTotemDollSkinType(), config::getStandardTotemDollSkinType, (value) -> {
					config.setStandardTotemDollSkinType(value);
					renderer.updateDoll();
					standardDollSkinDataOption.setAvailable(value.isNeedData());
				}, true)
				.withController(TotemDollSkinType.class)
				.build();

		return SimpleGroup.startBuilder("standard_doll_skin")
				.withCustomDescription(renderer)
				.options(
						standardDollSkinTypeOption,
						standardDollSkinDataOption
				).build();
	}

	private static OptionGroup getStandardDollModelGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config, TotemDollPreviewRenderer renderer) {
		Option<Identifier> standardDollModelPathOption = SimpleOption.<Identifier>startBuilder("standard_doll_model_path")
				.withCustomDescription(renderer)
				.withBinding(config.getSelectedStandardTotemDollModelValue(), config::getStandardTotemDollModelValue, (value) -> {
					config.setStandardTotemDollModelValue(value);
					renderer.updateDollState(true);
					for (TotemDollData data : TotemDollManager.getAllLoadedDolls()) {
						data.setShouldRecreateStandardModel(true);
					}
				}, true)
				.getOptionBuilder()
				.controller(TotemDollModelControllerBuilder::create)
				.build();
		Option<TotemDollArmsType> standardDollModelArmsTypeOption = SimpleOption.<TotemDollArmsType>startBuilder("standard_doll_model_arms_type")
				.withCustomDescription(renderer)
				.withBinding(defConfig.getStandardTotemDollArmsType(), config::getStandardTotemDollArmsType, (value) -> {
					config.setStandardTotemDollArmsType(value);
					renderer.updateDollState(false);
				}, true)
				.withController(TotemDollArmsType.class)
				.build();

		standardDollModelPathOption.setAvailable(!config.isUseVanillaTotemModel());
		standardDollModelArmsTypeOption.setAvailable(!config.isUseVanillaTotemModel());

		return SimpleGroup.startBuilder("standard_doll_model")
				.withCustomDescription(renderer)
				.options(
						standardDollModelPathOption,
						standardDollModelArmsTypeOption
				).build();
	}
}
