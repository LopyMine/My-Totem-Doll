package net.lopymine.mtd.extension;

import net.minecraft.client.resources.model.cuboid.*;

public class ModelTransformationExtension {

	public static ItemTransform getTl(ItemTransforms transform) {
		return transform.thirdPersonLeftHand();
	}

	public static ItemTransform getTr(ItemTransforms transform) {
		return transform.thirdPersonRightHand();
	}

	public static ItemTransform getFl(ItemTransforms transform) {
		return transform.firstPersonLeftHand();
	}

	public static ItemTransform getFr(ItemTransforms transform) {
		return transform.firstPersonRightHand();
	}

	public static ItemTransform getHead(ItemTransforms transform) {
		return transform.head();
	}

	public static ItemTransform getGui(ItemTransforms transform) {
		return transform.gui();
	}

	public static ItemTransform getGround(ItemTransforms transform) {
		return transform.ground();
	}

	public static ItemTransform getFixed(ItemTransforms transform) {
		return transform.fixed();
	}

	public static ItemTransform getOnShelf(ItemTransforms transform) {
		return transform.fixedFromBottom();
	}

}
