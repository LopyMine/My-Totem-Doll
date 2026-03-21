package net.lopymine.mtd.extension;

import net.minecraft.client.model.geom.PartPose;

public class ModelTransformExtension {

	public static PartPose subtract(PartPose root, PartPose parent) {
		return PartPose.offsetAndRotation(
				getPivotX(root) - getPivotX(parent),
				getPivotY(root) - getPivotY(parent),
				getPivotZ(root) - getPivotZ(parent),
				getPitch(root),
				getYaw(root),
				getRoll(root)
		);
	}

	public static float getPivotX(PartPose transform) {
		return transform.x();
	}

	public static float getPivotY(PartPose transform) {
		return transform.y();
	}

	public static float getPivotZ(PartPose transform) {
		return transform.z();
	}

	public static float getPitch(PartPose transform) {
		return transform.xRot();
	}

	public static float getYaw(PartPose transform) {
		return transform.yRot();
	}

	public static float getRoll(PartPose transform) {
		return transform.zRot();
	}

	public static PartPose getBlockBenchedModelTransform(PartPose transform) {
		return PartPose.offsetAndRotation(-getPivotX(transform), -getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
	}

	public static String asString(PartPose transform) {
		return "%s %s %s | %s %s %s".formatted(getPivotX(transform), getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
	}

}
