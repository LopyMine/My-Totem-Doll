package net.lopymine.mtd.doll.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.AtlasSprite;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.rendering.*;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.model.TotemDollModel.Drawer;
import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.optimization.TotemDollRenderRequestsCollector;
import net.lopymine.mtd.thing.ThingMarks;
import net.lopymine.mtd.utils.*;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;


@ExtensionMethod({ItemStackExtension.class, DrawContextExtension.class})
public class TotemDollRenderer {

	public static boolean sentRenderRequest(PoseStack matrices, ItemStack stack, DollRenderContext context, int light, int overlay, int outlineColor, @Nullable MultiBufferSource provider) {
		if (canRender(stack)) {
			TotemDollData totemDollData = stack.getTotemDollData(false);
			TotemDollRenderRequestsCollector.getInstance().requestRender(matrices, totemDollData, stack.getPlayerEntity(), context, light, overlay, outlineColor, provider);
			if (!ThingMarks.WORLD_RENDERING.get().isMarked()) {
				TotemDollRenderRequestsCollector.getInstance().renderStates();
			}
			return true;
		}
		return false;
	}

	public static void renderDoll(PoseStack matrices, ItemStack stack, DollRenderContext context, MultiBufferSource vertexConsumers, int light, int overlay) {
		renderDoll(matrices, stack.getTotemDollData(), stack.getPlayerEntity(), context, vertexConsumers, light, overlay);
	}

	public static void renderDoll(PoseStack matrices, TotemDollData totemDollData, AbstractClientPlayer holdingPlayer, DollRenderContext context, MultiBufferSource vertexConsumers, int light, int overlay) {
		DollRenderContext renderContext = context == DollRenderContext.D_NONE ? DollRenderContext.D_GUI : context;
		beforeDollRendered(renderContext, holdingPlayer, totemDollData);
		matrices.pushPose();

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
		matrices.popPose();
	}

	public static void renderPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable TotemDollData data) {
		renderPreview(context, x, y, width, height, size, data, DollRenderContext.D_PREVIEW);
	}

	public static void renderPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable TotemDollData data, DollRenderContext renderContext) {
		if (data == null) {
			long currentTime = Util.getMillis();
			float rotationSpeed = 0.05f;
			float rotation = (currentTime * rotationSpeed) % 360;
			context.guiRenderState.addPicturesInPictureState(new net.lopymine.mtd.doll.renderer.special.ItemGuiRenderState(Items.TOTEM_OF_UNDYING.getDefaultInstance(), x, y, width, height, size, Axis.YP.rotationDegrees(rotation), context.scissorStack.peek()));
		} else {
			data.getRenderProperties().setRenderContext(renderContext);
			context.guiRenderState.addPicturesInPictureState(net.lopymine.mtd.doll.renderer.special.TotemDollRenderState.getPreview(data, x, y, width, height, size, context.scissorStack.peek()));
		}
	}

	public static void renderDataPreview(PoseStack matrices, BufferSource consumers, Runnable draw, float size, @NotNull TotemDollData data) {
		float i = (size / 2F);

		long currentTime = Util.getMillis();
		float rotationSpeed = 0.05f;

		float rotation = (currentTime * rotationSpeed) % 360;

		LightningUtils.disable3dLighting();
		matrices.pushPose();
		matrices.scale(-i, -i, i);
		matrices.mulPose(Axis.YP.rotationDegrees(rotation));
		matrices.translate(-0.5F, -1.0F, -0.5F);
		TotemDollRenderer.render(matrices, consumers, 15728880, OverlayTexture.NO_OVERLAY, data);
		matrices.popPose();
		draw.run();
		LightningUtils.enable3dLighting();
	}

	public static void renderInHand(boolean leftHanded, boolean firstPerson, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, TotemDollData totemDollData) {
		matrices.pushPose();

		if (firstPerson) {
			MyTotemDollConfig config = MyTotemDollConfig.getInstance();
			RenderingConfig renderingConfig = config.getRenderingConfig();
			HandRenderingConfig handRenderingConfig = leftHanded ? renderingConfig.getLeftHandConfig() : renderingConfig.getRightHandConfig();

			matrices.translate((handRenderingConfig.getOffsetZ() / 100F) * (leftHanded ? 1 : -1), handRenderingConfig.getOffsetY() / 100F, handRenderingConfig.getOffsetX() / 100F);

			matrices.translate(0.5F, 0.5F, 0.5F);

			double scale = handRenderingConfig.getScale();
			matrices.scale((float) scale, (float) scale, (float) scale);
			matrices.mulPose(Axis.XP.rotationDegrees((float) handRenderingConfig.getRotationX()));
			matrices.mulPose(Axis.YP.rotationDegrees((float) handRenderingConfig.getRotationY() * (leftHanded ? -1 : 1)));
			matrices.mulPose(Axis.ZP.rotationDegrees((float) handRenderingConfig.getRotationZ() * (leftHanded ? -1 : 1)));

			matrices.translate(-0.5F, -0.5F, -0.5F);
		}

		TotemDollRenderer.render(matrices, vertexConsumers, light, overlay, totemDollData);
		matrices.popPose();
	}

	public static void render(PoseStack matrices, MultiBufferSource provider, int light, int overlay, TotemDollData totemDollData) {
		TotemDollSprites textures = totemDollData.getSpritesToRender();
		AtlasSprite skinSprite = textures.getSkinSprite();
		AtlasSprite capeSprite = textures.getCapeSprite();
		AtlasSprite elytraSprite = textures.getElytraSprite();
		TotemDollModel model = totemDollData.getModelToRender();

		String nickname = totemDollData.getNickname();

		if (nickname != null && (nickname.equalsIgnoreCase("dinnerbone") || nickname.equalsIgnoreCase("grumm"))) {
			matrices.translate(0.5F, 1.0F, 0.5F);
			matrices.mulPose(Axis.ZP.rotationDegrees(180));
			matrices.translate(-0.5F, -1.0F, -0.5F);
		}

		matrices.pushPose();
		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.scale(-1.0F, -1.0F, 1.0F); // - - 0
		matrices.translate(-0.5F, -0.5F, -0.5F);

		Drawer drawer = model.getDrawer();

		if (nickname != null && nickname.equals("deadmau5")) {
			drawer.requestDrawingPartWithSprite("ears", skinSprite);
		}

		if (capeSprite != null && capeSprite.isUploaded()) {
			drawer.requestDrawingPartWithSprite("cape", capeSprite);
		}

		if (elytraSprite.isUploaded()) {
			drawer.requestDrawingPartWithSprite("elytra", elytraSprite);
		}

		drawer.draw(matrices, provider, skinSprite, light, overlay, -1);

		matrices.popPose();
	}

	private static void beforeDollRendered(@Nullable DollRenderContext context, AbstractClientPlayer playerEntity, TotemDollData totemDollData) {
		ProfilerFiller profiler = ProfilerUtils.getProfiler();
		profiler.popPush(MyTotemDoll.MOD_ID);

		if (context == DollRenderContext.D_GUI && MyTotemDollConfig.getInstance().getStandardTotemDollSkinType() == TotemDollSkinType.HOLDING_PLAYER) {
			playerEntity = Minecraft.getInstance().player;
		}

		if (StandardTotemDollManager.getStandardDoll().equals(totemDollData)) {
			TotemDollRenderer.prepareStandardDollForRendering(playerEntity, totemDollData);
		}
	}

	private static void prepareStandardDollForRendering(AbstractClientPlayer playerEntity, TotemDollData totemDollData) {
		if (playerEntity != null && MyTotemDollConfig.getInstance().getStandardTotemDollSkinType() == TotemDollSkinType.HOLDING_PLAYER) {
			if (!playerEntity.equals(Minecraft.getInstance().player) && playerEntity.isInvisibleTo(Minecraft.getInstance().player)) {
				return;
			}
			totemDollData.setFrameSprites(playerEntity);
		}
	}

	private static void afterDollRenderer() {
		ProfilerFiller profiler = ProfilerUtils.getProfiler();
		profiler.pop();
	}

	public static boolean canRender(@Nullable ItemStack stack) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return false;
		}
		if (stack.hasModdedModel()) {
			return false;
		}
		Component realCustomName = stack.getRealCustomName();
		boolean standardDollWithoutName = realCustomName == null;
		if (standardDollWithoutName && MyTotemDollConfig.getInstance().isUseVanillaTotemModel()) {
			return false;
		}
		return !TotemDollPlugin.work(realCustomName);
	}
}
