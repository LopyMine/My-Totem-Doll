package net.lopymine.mtd.doll.renderer;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.AtlasSprite;
import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.*;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.rendering.*;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.model.TotemDollModel.Drawer;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;

import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;


@ExtensionMethod({ItemStackExtension.class, DrawContextExtension.class})
public class TotemDollRenderer {

	public static boolean rendered(MatrixStack matrices, ItemStack stack, DollRenderContext context, VertexConsumerProvider vertexConsumers, int light, int uv) {
		if (canRender(stack)) {
			return TotemDollRenderer.renderDoll(matrices, stack, context, vertexConsumers, light, uv);
		}
		return false;
	}

	public static boolean renderedFloatingDoll(MatrixStack matrices, ItemStack stack, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (canRender(stack)) {
			return renderDoll(matrices, stack, DollRenderContext.D_FLOATING, vertexConsumers, light, overlay);
		}
		return false;
	}

	public static boolean renderDoll(MatrixStack matrices, ItemStack stack, DollRenderContext context, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		DollRenderContext renderContext = context == DollRenderContext.D_NONE ? DollRenderContext.D_GUI : context;
		TotemDollData totemDollData = stack.getTotemDollData();

		if (!beforeDollRendered(renderContext, stack, totemDollData)) {
			return false;
		}

		matrices.push();
		renderContext.apply(totemDollData.getModelToRender().getMain(), matrices);
		totemDollData.getRenderProperties().setRenderContext(renderContext);
		matrices.translate(-0.5F, -1.0F, -0.5F);

		switch (renderContext) {
			case D_FIRST_PERSON_LEFT_HAND,
			     D_FIRST_PERSON_RIGHT_HAND -> TotemDollRenderer.renderInHand(renderContext.isLeftHanded(), true, matrices, vertexConsumers, light, overlay, totemDollData);
			case D_THIRD_PERSON_LEFT_HAND,
			     D_THIRD_PERSON_RIGHT_HAND -> TotemDollRenderer.renderInHand(renderContext.isLeftHanded(), false, matrices, vertexConsumers, light, overlay, totemDollData);
			default -> TotemDollRenderer.render(matrices, vertexConsumers, light, overlay, totemDollData);
		}

		afterDollRenderer();

		matrices.pop();
		return true;
	}

	public static void renderPreview(DrawContext context, int x, int y, int width, int height, float size, @Nullable TotemDollData data) {
		renderPreview(context, x, y, width, height, size, data, DollRenderContext.D_PREVIEW);
	}

	public static void renderPreview(DrawContext context, int x, int y, int width, int height, float size, @Nullable TotemDollData data, DollRenderContext renderContext) {
		if (data == null) {
			//? if >=1.21.6 {
			long currentTime = Util.getMeasuringTimeMs();
			float rotationSpeed = 0.05f;
			float rotation = (currentTime * rotationSpeed) % 360;
			context.state.addSpecialElement(new net.lopymine.mtd.doll.renderer.special.ItemGuiRenderState(Items.TOTEM_OF_UNDYING.getDefaultStack(), x, y, width, height, size, RotationAxis.POSITIVE_Y.rotationDegrees(rotation), context.scissorStack.peekLast()));
			//?} else {
			/*renderVanillaTotemPreview(context, x, y, width, height, size);
			*///?}
		} else {
			data.getRenderProperties().setRenderContext(renderContext);
			//? if >=1.21.6 {
			context.state.addSpecialElement(net.lopymine.mtd.doll.renderer.special.TotemDollRenderState.getPreview(data, x, y, width, height, size, context.scissorStack.peekLast()));
			//?} else {
			/*context.getMatrices().push();
			int centerX = x + (width / 2);
			int centerY = y + (height / 2);
			context.getMatrices().translate(centerX, centerY, 300F);
			context.getMatrices().scale(-1.0F, 1.0F, 1.0F);
			renderDataPreview(context.getMatrices(), context.vertexConsumers, context::draw, size, data);
			context.getMatrices().pop();
			*///?}
		}
	}

	public static void renderDataPreview(MatrixStack matrices, Immediate consumers, Runnable draw, float size, @NotNull TotemDollData data) {
		float i = (size / 2F);

		long currentTime = Util.getMeasuringTimeMs();
		float rotationSpeed = 0.05f;

		float rotation = (currentTime * rotationSpeed) % 360;

		LightningUtils.disable3dLighting();
		matrices.push();
		matrices.scale(-i, -i, i);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		matrices.translate(-0.5F, -1.0F, -0.5F);
		TotemDollRenderer.render(matrices, consumers, 15728880, OverlayTexture.DEFAULT_UV, data);
		matrices.pop();
		draw.run();
		LightningUtils.enable3dLighting();
	}

	//? if <=1.21.5 {
	/*public static void renderVanillaTotemPreview(DrawContext context, int x, int y, int width, int height, float size) {
		float i = (size / 2F);
		int centerX = x + (width / 2);
		int centerY = y + (height / 2);
		long currentTime = Util.getMeasuringTimeMs();
		float rotationSpeed = 0.05f;

		float rotation = (currentTime * rotationSpeed) % 360;

		float v = i / 16;
		float d = i / 2;

		context.push();
		context.translate(centerX - d, centerY - d, 400F);
		context.translate(d, d, 0F);
		context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
		context.translate(-d, -d, 0F);
		context.scale(v, v, v);
		context.translate(0F, 0F, -150F); // I hate this
		context.drawItemWithoutEntity(Items.TOTEM_OF_UNDYING.getDefaultStack(), 0, 0);
		context.pop();
	}
	*///?}

	public static void renderInHand(boolean leftHanded, boolean firstPerson, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, TotemDollData totemDollData) {
		matrices.push();

		if (firstPerson) {
			MyTotemDollConfig config = MyTotemDollClient.getConfig();
			RenderingConfig renderingConfig = config.getRenderingConfig();
			HandRenderingConfig handRenderingConfig = leftHanded ? renderingConfig.getLeftHandConfig() : renderingConfig.getRightHandConfig();

			matrices.translate((handRenderingConfig.getOffsetZ() / 100F) * (leftHanded ? 1 : -1), handRenderingConfig.getOffsetY() / 100F, handRenderingConfig.getOffsetX() / 100F);

			matrices.translate(0.5F, 0.5F, 0.5F);

			double scale = handRenderingConfig.getScale();
			matrices.scale((float) scale, (float) scale, (float) scale);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) handRenderingConfig.getRotationX()));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) handRenderingConfig.getRotationY() * (leftHanded ? -1 : 1)));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) handRenderingConfig.getRotationZ() * (leftHanded ? -1 : 1)));

			matrices.translate(-0.5F, -0.5F, -0.5F);
		}

		TotemDollRenderer.render(matrices, vertexConsumers, light, overlay, totemDollData);
		matrices.pop();
	}

	public static void render(MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, TotemDollData totemDollData) {
		TotemDollSprites textures = totemDollData.getTexturesToRender();
		AtlasSprite skinSprite = textures.getSkinSprite();
		AtlasSprite capeSprite = textures.getCapeSprite();
		AtlasSprite elytraSprite = textures.getElytraSprite();
		TotemDollModel model = totemDollData.getModelToRender();

		String nickname = totemDollData.getNickname();

		if (nickname != null && (nickname.equalsIgnoreCase("dinnerbone") || nickname.equalsIgnoreCase("grumm"))) {
			matrices.translate(0.5F, 1.0F, 0.5F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
			matrices.translate(-0.5F, -1.0F, -0.5F);
		}

		matrices.push();
		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.scale(-1.0F, -1.0F, 1.0F); // - - 0
		matrices.translate(-0.5F, -0.5F, -0.5F);

		Drawer drawer = model.getDrawer();

		if (nickname != null && nickname.equals("deadmau5")) {
			drawer.requestDrawingPartWithTexture("ears", skinSprite);
		}

		if (capeSprite != null && capeSprite.isUploaded()) {
			drawer.requestDrawingPartWithTexture("cape", capeSprite);
		}

		if (elytraSprite.isUploaded()) {
			drawer.requestDrawingPartWithTexture("elytra", elytraSprite);
		}

		drawer.draw(matrices, provider, skinSprite, light, overlay, /*? if >=1.21 {*/ -1 /*?} else {*/ /*1.0F, 1.0F, 1.0F, 1.0F *//*?}*/);

		matrices.pop();
	}

	private static void prepareStandardDollForRendering(ItemStack stack, TotemDollData totemDollData) {
		AbstractClientPlayerEntity playerEntity = stack.getPlayerEntity();
		if (playerEntity != null && MyTotemDollClient.getConfig().getStandardTotemDollSkinType() == TotemDollSkinType.HOLDING_PLAYER) {
			totemDollData.setFrameTextures(TotemDollSprites.of(playerEntity));
		}
	}

	private static boolean beforeDollRendered(@Nullable DollRenderContext context, ItemStack stack, TotemDollData totemDollData) {
		Profiler profiler = ProfilerUtils.getProfiler();
		profiler.swap(MyTotemDoll.MOD_ID);
		if (context == DollRenderContext.D_GUI) {
			stack.setPlayerEntity(MinecraftClient.getInstance().player);
		}

		if (StandardTotemDollManager.getStandardDoll().equals(totemDollData)) {
			TotemDollRenderer.prepareStandardDollForRendering(stack, totemDollData);
		}

		return true;
	}

	private static void afterDollRenderer() {
		Profiler profiler = ProfilerUtils.getProfiler();
		profiler.pop();
	}

	public static boolean canRender(@Nullable ItemStack stack) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return false;
		}
		if (stack.hasModdedModel()) {
			return false;
		}
		Text realCustomName = stack.getRealCustomName();
		boolean standardDollWithoutName = realCustomName == null;
		if (standardDollWithoutName && MyTotemDollClient.getConfig().isUseVanillaTotemModel()) {
			return false;
		}
		return !TotemDollPlugin.work(realCustomName);
	}
}
