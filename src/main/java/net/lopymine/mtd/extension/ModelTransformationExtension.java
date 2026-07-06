package net.lopymine.mtd.extension;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

public class ModelTransformationExtension {
	
	public static ItemTransform getTl(ItemTransforms transform) {
		return  transform.thirdPersonLeftHand; 
	}

	public static ItemTransform getTr(ItemTransforms transform) {
		return  transform.thirdPersonRightHand; 
	}

	public static ItemTransform getFl(ItemTransforms transform) {
		return  transform.firstPersonLeftHand; 
	}

	public static ItemTransform getFr(ItemTransforms transform) {
		return  transform.firstPersonRightHand; 
	}

	public static ItemTransform getHead(ItemTransforms transform) {
		return  transform.head; 
	}

	public static ItemTransform getGui(ItemTransforms transform) {
		return  transform.gui; 
	}

	public static ItemTransform getGround(ItemTransforms transform) {
		return  transform.ground; 
	}

	public static ItemTransform getFixed(ItemTransforms transform) {
		return  transform.fixed; 
	}


//	public static ModelTransformation getBlockBenchedModelTransformation(ModelTransformation transform) {
//		return ModelTransformation.of(-getPivotX(transform), -getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
//	}
//
//	public static String asString(ModelTransformation transform) {
//		return "%s %s %s | %s %s %s".formatted(getPivotX(transform), getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
//	}

}
