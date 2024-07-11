package net.lopymine.mtd.modmenu.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.sub.*;
import net.lopymine.mtd.modmenu.yacl.custom.screen.MyTotemDollYACLScreen;
import net.lopymine.mtd.modmenu.yacl.simple.*;
import net.lopymine.mtd.utils.ModMenuUtils;

import java.util.function.Function;

public class YACLConfigurationScreen {

	private static final Function<Boolean, Text> ENABLED_OR_DISABLE_FORMATTER = ModMenuUtils.getEnabledOrDisabledFormatter();

	private YACLConfigurationScreen() {
		throw new IllegalStateException("Screen class");
	}

	public static Screen createScreen(Screen parent) {
		MyTotemDollConfig defConfig = new MyTotemDollConfig();
		MyTotemDollConfig config = MyTotemDollClient.getConfig();

		return SimpleYACLScreenBuilder.startBuilder(parent, config::save)
				.categories(
						getGeneralCategory(defConfig, config),
						getRenderingCategory(defConfig, config)
				).build();
	}

	private static ConfigCategory getRenderingCategory(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleRenderingCategoryBuilder.startBuilder().groups(
				getRenderingRightHandGroup(defConfig, config),
				getRenderingLeftHandGroup(defConfig, config)
		).build();
	}

	private static OptionGroup getRenderingRightHandGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return getRenderingHandGroup(HandGroup.RIGHT_HAND, defConfig, config);
	}

	private static OptionGroup getRenderingLeftHandGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return getRenderingHandGroup(HandGroup.LEFT_HAND, defConfig, config);
	}

	private static OptionGroup getRenderingHandGroup(HandGroup handGroup, MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		RenderingConfig defRenderingConfig = defConfig.getRenderingConfig();
		RenderingConfig renderingConfig = config.getRenderingConfig();

		HandRenderingConfig defHandConfig = handGroup.right() ? defRenderingConfig.getRightHandConfig() : defRenderingConfig.getLeftHandConfig();
		HandRenderingConfig handConfig = handGroup.right() ? renderingConfig.getRightHandConfig() : renderingConfig.getLeftHandConfig();

		return SimpleGroupBuilder.startBuilder(handGroup.getGroupId()).options(
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"scale",
								0.0D,
								2.0D,
								0.01D,
								defHandConfig.getScale(),
								handConfig::getScale,
								handConfig::setScale
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"rotation_x",
								-180.0D,
								180.0D,
								0.01D,
								defHandConfig.getRotationX(),
								handConfig::getRotationX,
								handConfig::setRotationX
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"rotation_y",
								-180.0D,
								180.0D,
								0.01D,
								defHandConfig.getRotationY(),
								handConfig::getRotationY,
								handConfig::setRotationY
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"rotation_z",
								-180.0D,
								180.0D,
								0.01D,
								defHandConfig.getRotationZ(),
								handConfig::getRotationZ,
								handConfig::setRotationZ
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"offset_x",
								-100.0D,
								100.0D,
								0.01D,
								defHandConfig.getOffsetX(),
								handConfig::getOffsetX,
								handConfig::setOffsetX
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"offset_y",
								-100.0D,
								100.0D,
								0.01D,
								defHandConfig.getOffsetY(),
								handConfig::getOffsetY,
								handConfig::setOffsetY
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getDoubleOptionAsSliderWithoutDescription(
								"offset_z",
								-100.0D,
								100.0D,
								0.01D,
								defHandConfig.getOffsetZ(),
								handConfig::getOffsetZ,
								handConfig::setOffsetZ
						)
						.instant(true)
						.build(),
				SimpleOptionBuilder.getButtonOption(
						(handGroup.right() ? "copy_left_hand_settings" : "copy_right_hand_settings"),
						((yaclScreen, buttonOption) -> {
							HandRenderingConfig leftHandConfig = renderingConfig.getLeftHandConfig();
							HandRenderingConfig rightHandConfig = renderingConfig.getRightHandConfig();
							if (handGroup.right()) {
								rightHandConfig.copy(leftHandConfig);
							} else {
								leftHandConfig.copy(rightHandConfig);
							}
							OptionUtils.forEachOptions(yaclScreen.config, Option::forgetPendingValue);
						}))
		).build();
	}

	private static ConfigCategory getGeneralCategory(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleCategoryBuilder.startBuilder("general")
				.options(
						SimpleOptionBuilder.getBooleanOptionWithDescription(
										"mod_enabled",
										defConfig.isModEnabled(),
										config::isModEnabled,
										config::setModEnabled,
										ENABLED_OR_DISABLE_FORMATTER::apply,
										SimpleContent.IMAGE,
										700, 1417
								)
								.instant(true)
								.build(),
						SimpleOptionBuilder.getBooleanOptionWithDescription(
										"debug_log_enabled",
										defConfig.isDebugLogEnabled(),
										config::isDebugLogEnabled,
										config::setDebugLogEnabled,
										ENABLED_OR_DISABLE_FORMATTER::apply,
										SimpleContent.NONE,
										0, 0
								)
								.instant(true)
								.build())
				.build();
	}

	public static boolean notOpen(Screen currentScreen) {
		return !(currentScreen instanceof MyTotemDollYACLScreen);
//		if (currentScreen == null) {
//			return true;
//		}
//		return !ModMenuUtils.getModTitle().getString().equals(currentScreen.getTitle().getString());
	}

	public static Text getRenderingCategoryTitle() {
		return ModMenuUtils.getName(ModMenuUtils.getCategoryKey("rendering"));
	}

	@Getter
	private enum HandGroup {
		LEFT_HAND("left_hand"),
		RIGHT_HAND("right_hand");

		private final String groupId;

		HandGroup(String groupId) {
			this.groupId = groupId;
		}

		public boolean right() {
			return this == RIGHT_HAND;
		}
	}
}


