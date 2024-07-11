package net.lopymine.mtd.doll.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.cache.CachedSkinsManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.sub.*;
import net.lopymine.mtd.doll.layer.DollLayers;
import net.lopymine.mtd.doll.model.DollModel;
import net.lopymine.mtd.doll.tag.DollTagManager;

import org.jetbrains.annotations.*;

//? if >=1.20.5 {
import net.minecraft.component.DataComponentTypes;
 //?}

//? if >=1.20.2 {
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
//?} else {
/*import net.lopymine.mtd.utils.SkinTextures;
import net.lopymine.mtd.utils.SkinTextures.Model;
*///?}

public class DollRenderer {

	private static final float TEST = 0.0F;

	public static void renderFloatingDoll(MatrixStack matrices, EntityModelLoader modelLoader, ItemStack stack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.translate(-0.5F, -0.5F, -0.5F);

		Pair<String, SkinTextures> skinTextures = DollRenderer.parseSkinTextures(stack);
		DollModel dollModel = DollRenderer.bakeDoll(modelLoader, skinTextures.getRight());

		DollRenderer.renderAsFloating(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);

		matrices.pop();
	}

	public static void renderDoll(MatrixStack matrices, EntityModelLoader entityModelLoader, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model) {
		matrices.push();
		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
		matrices.translate(-0.5F, -0.5F, -0.5F);

		Pair<String, SkinTextures> skinTextures = DollRenderer.parseSkinTextures(stack);
		DollModel dollModel = DollRenderer.bakeDoll(entityModelLoader, skinTextures.getRight());

		switch (renderMode) {
			case GUI -> DollRenderer.renderInGui(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case HEAD -> DollRenderer.renderInHead(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case FIXED -> DollRenderer.renderFixed(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case GROUND -> DollRenderer.renderOnGround(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case FIRST_PERSON_LEFT_HAND -> DollRenderer.renderInHand(true, true, matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case FIRST_PERSON_RIGHT_HAND -> DollRenderer.renderInHand(false, true, matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case THIRD_PERSON_LEFT_HAND -> DollRenderer.renderInHand(true, false, matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case THIRD_PERSON_RIGHT_HAND -> DollRenderer.renderInHand(false, false, matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
			case NONE -> DollRenderer.render(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);
		}

		matrices.pop();
	}

	private static void renderInHand(boolean leftHanded, boolean firstPerson, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		float xOffset;
		float yOffset;
		float zOffset;

		if (firstPerson) {
			xOffset = 1.75F;
			yOffset = 0.3F;
			zOffset = -1.7F;
			float zRotation = 20F;

			MyTotemDollConfig config = MyTotemDollClient.getConfig();
			RenderingConfig renderingConfig = config.getRenderingConfig();
			HandRenderingConfig handRenderingConfig = leftHanded ? renderingConfig.getLeftHandConfig() : renderingConfig.getRightHandConfig();

			matrices.push();
			matrices.translate((leftHanded ? xOffset : -xOffset), yOffset, zOffset);
			matrices.translate((handRenderingConfig.getOffsetZ() / 100F) * (leftHanded ? 1 : -1), handRenderingConfig.getOffsetY() / 100F, handRenderingConfig.getOffsetX() / 100F);
			matrices.translate(0.5F, 0.35F, 0.5F);
			matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees((leftHanded ? -zRotation : zRotation)));

			double scale = handRenderingConfig.getScale();
			matrices.scale((float) scale, (float) scale, (float) scale);

			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) handRenderingConfig.getRotationX()));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) handRenderingConfig.getRotationY() * (leftHanded ? -1 : 1)));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) handRenderingConfig.getRotationZ() * (leftHanded ? -1 : 1)));
			matrices.translate(-0.5F, -0.35F, -0.5F);

		} else {
			xOffset = 0.18F;
			yOffset = 0.35F;
			zOffset = 0.25F;

			matrices.push();

			matrices.translate(xOffset, yOffset, zOffset);
			matrices.scale(0.65F, 0.65F, 0.65F);
		}

		DollRenderer.render(matrices, vertexConsumers, light, overlay, model, skinTextures);
		matrices.pop();
	}

	private static void renderOnGround(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		matrices.push();

		matrices.translate(0.1F, 0.2F, 0.85F);
		matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
		matrices.scale(0.7F, 0.7F, 0.7F);

		DollRenderer.render(matrices, vertexConsumers, light, overlay, model, skinTextures);

		matrices.pop();
	}

	private static void renderFixed(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		matrices.push();

		matrices.translate(0.0F, 0.0F, 0.8F);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
		matrices.translate(0.0F, 0.0F, -1.2F);

		model.getCape().pitch = 0.2F;

		DollRenderer.render(matrices, vertexConsumers, light, overlay, model, skinTextures);

		matrices.pop();
	}

	public static void renderInHead(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		matrices.push();
		matrices.translate(0.0F, 0.0F, -0.2F);
		DollRenderer.render(matrices, vertexConsumers, light, overlay, model, skinTextures);
		matrices.pop();
	}

	private static void renderInGui(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		//? if >=1.20.5 {
		DiffuseLighting.method_34742();
		//?} else {
		/*Vector3f vec = (new Vector3f(0.0F, 1.0F, 1.0F)).normalize();
		Vector3f vec2 = (new Vector3f(0.0F, 1.0F, 1.0F)).normalize();

		RenderSystem.setShaderLights(vec, vec2);
		*///?}

		matrices.push();

		float yOffset = skinTextures.getRight().capeTexture() != null ? 0.37F : 0.3F;
		float xOffset = 0.25F;
		String nickname = skinTextures.getLeft();
		if (nickname != null && nickname.equals("deadmau5")) {
			yOffset = 0.2F;
			xOffset = 0.3F;
		}
		matrices.translate(xOffset, yOffset, 0.25F);
		matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(20));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(10));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(20));
		matrices.scale(0.5F, 0.5F, 0.5F);
		matrices.translate(-0.1F, -yOffset, -0.25F);

		DollRenderer.render(matrices, vertexConsumers, light, overlay, model, skinTextures);

		matrices.pop();
	}

	private static void renderAsFloating(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel dollModel, Pair<String, SkinTextures> skinTextures) {
		Vector3f vec = (new Vector3f(0.0F, 0.0F, 0.5f)).normalize();
		Vector3f vec2 = (new Vector3f(0.0F, 0.0F, 0.5f)).normalize();

		RenderSystem.setShaderLights(vec, vec2);

		matrices.push();

		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180F));
		matrices.translate(-0.5F, -0.5F, -0.5F);
		matrices.scale(0.8F, 0.8F, 0.8F);
		matrices.translate(0.1F, 0.0F, 0.0F);

		DollRenderer.render(matrices, vertexConsumers, light, overlay, dollModel, skinTextures);

		matrices.pop();
	}

	private static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, DollModel model, Pair<String, SkinTextures> skinTextures) {
		SkinTextures textures = skinTextures.getRight();
		String nickname = skinTextures.getLeft();

		if (nickname != null && nickname.equals("dinnerbone")) {
			matrices.translate(0.5F, 0.5F, 0.5F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
			matrices.translate(-0.5F, -0.5F, -0.5F);
		}

		matrices.push();
		matrices.translate(0.5F, 0.0F, 0.5F);
		matrices.scale(1.0F, -1.0F, -1.0F);

		VertexConsumer skinVertexConsumer = vertexConsumers.getBuffer(model.getLayer(textures.texture()));
		model.render(matrices, skinVertexConsumer, light, overlay,  /*? if >=1.21 {*/ -1 /*?} else {*/ /*1.0F, 1.0F, 1.0F, 1.0F *//*?}*/);

		if (nickname != null && nickname.equals("deadmau5")) {
			model.renderEars(matrices, skinVertexConsumer, light, overlay, /*? if >=1.21 {*/ -1 /*?} else {*/ /*1.0F, 1.0F, 1.0F, 1.0F *//*?}*/);
		}

		Identifier capeTexture = textures.capeTexture();
		if (capeTexture != null) {
			VertexConsumer capeVertexConsumer = vertexConsumers.getBuffer(model.getLayer(capeTexture));
			model.renderCape(matrices, capeVertexConsumer, light, overlay,  /*? if >=1.21 {*/ -1 /*?} else {*/ /*1.0F, 1.0F, 1.0F, 1.0F *//*?}*/);
		}

		matrices.pop();
	}

	private static Pair<@Nullable String, @NotNull SkinTextures> parseSkinTextures(ItemStack stack) {
		Text customName = /*? if >=1.20.5 {*/stack.getComponents().get(DataComponentTypes.CUSTOM_NAME); /*?} else {*/ /*stack.hasCustomName() ? stack.getName() : null; *//*?}*/

		if (customName != null) {
			String nickname = DollTagManager.getOnlyNickname(customName.getString());
			return new Pair<>(nickname, CachedSkinsManager.getSkin(nickname));
		}
		return new Pair<>(null, CachedSkinsManager.DEFAULT_DOLL_TEXTURES);
	}

	private static DollModel bakeDoll(EntityModelLoader modelLoader, SkinTextures skinTextures) {
		EntityModelLayer layer = skinTextures.model() == Model.WIDE ? DollLayers.STEVE_MODEL_LAYER : DollLayers.ALEX_MODEL_LAYER;
		ModelPart modelPart = modelLoader.getModelPart(layer);
		return new DollModel(modelPart);

//		Be later :^
//
//		DollModel dollModel = new DollModel(modelPart);
//		if (customName != null) {
//			String nickname = customName.getString();
//			if (DollTagManager.canBeProcessed(nickname)) {
//				DollTagManager.process(nickname, dollModel);
//			}
//		}
//
//		return dollModel;
	}
}
