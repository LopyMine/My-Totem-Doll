package net.lopymine.mtd.extension;

import net.minecraft.client.render.model.json.*;

public class ModelTransformationExtension {
	
	public static Transformation getTl(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.thirdPersonLeftHand(); /*?} else {*/ /*transform.thirdPersonLeftHand; *//*?}*/
	}

	public static Transformation getTr(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.thirdPersonRightHand(); /*?} else {*/ /*transform.thirdPersonRightHand; *//*?}*/
	}

	public static Transformation getFl(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.firstPersonLeftHand(); /*?} else {*/ /*transform.firstPersonLeftHand; *//*?}*/
	}

	public static Transformation getFr(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.firstPersonRightHand(); /*?} else {*/ /*transform.firstPersonRightHand; *//*?}*/
	}

	public static Transformation getHead(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.head(); /*?} else {*/ /*transform.head; *//*?}*/
	}

	public static Transformation getGui(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.gui(); /*?} else {*/ /*transform.gui; *//*?}*/
	}

	public static Transformation getGround(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.ground(); /*?} else {*/ /*transform.ground; *//*?}*/
	}

	public static Transformation getFixed(ModelTransformation transform) {
		return /*? >=1.21.4 {*/ transform.fixed(); /*?} else {*/ /*transform.fixed; *//*?}*/
	}

	//? if >=1.21.9 {
	public static Transformation getOnShelf(ModelTransformation transform) {
		return transform.fixedFromBottom();
	}
	//?}

//	public static ModelTransformation getBlockBenchedModelTransformation(ModelTransformation transform) {
//		return ModelTransformation.of(-getPivotX(transform), -getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
//	}
//
//	public static String asString(ModelTransformation transform) {
//		return "%s %s %s | %s %s %s".formatted(getPivotX(transform), getPivotY(transform), getPivotZ(transform), getPitch(transform), getYaw(transform), getRoll(transform));
//	}

}
