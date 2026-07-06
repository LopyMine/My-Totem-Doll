package net.lopymine.mtd.yacl.category;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.rendering.*;
import net.lopymine.mtd.extension.SimpleOptionExtension;
import net.lopymine.mtd.yacl.custom.simple.custom.SimpleRenderingCategory;
import net.lopymine.mtd.yacl.custom.simple.main.*;

@ExtensionMethod(SimpleOptionExtension.class)
public class RenderingCategory {

	public static ConfigCategory get(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleRenderingCategory.startBuilder().groups(
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

		return SimpleGroup.startBuilder(handGroup.getGroupId()).options(
				SimpleOption.<Double>startBuilder("scale")
						.withController(0.0D, 2.0D, 0.01D)
						.withBinding(defHandConfig.getScale(), handConfig::getScale, handConfig::setScale, true)
						.build(),
				SimpleOption.<Double>startBuilder("rotation_x")
						.withController(-180D, 180D, 0.01D)
						.withBinding(defHandConfig.getRotationX(), handConfig::getRotationX, handConfig::setRotationX, true)
						.build(),
				SimpleOption.<Double>startBuilder("rotation_y")
						.withController(-180D, 180D, 0.01D)
						.withBinding(defHandConfig.getRotationY(), handConfig::getRotationY, handConfig::setRotationY, true)
						.build(),
				SimpleOption.<Double>startBuilder("rotation_z")
						.withController(-180D, 180D, 0.01D)
						.withBinding(defHandConfig.getRotationZ(), handConfig::getRotationZ, handConfig::setRotationZ, true)
						.build(),
				SimpleOption.<Double>startBuilder("offset_x")
						.withController(-500.0D, 500.0D, 0.01D)
						.withBinding(defHandConfig.getOffsetX(), handConfig::getOffsetX, handConfig::setOffsetX, true)
						.build(),
				SimpleOption.<Double>startBuilder("offset_y")
						.withController(-500.0D, 500.0D, 0.01D)
						.withBinding(defHandConfig.getOffsetY(), handConfig::getOffsetY, handConfig::setOffsetY, true)
						.build(),
				SimpleOption.<Double>startBuilder("offset_z")
						.withController(-500.0D, 500.0D, 0.01D)
						.withBinding(defHandConfig.getOffsetZ(), handConfig::getOffsetZ, handConfig::setOffsetZ, true)
						.build(),
				SimpleOption.startButtonBuilder(
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
						.build()
		).build();
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
