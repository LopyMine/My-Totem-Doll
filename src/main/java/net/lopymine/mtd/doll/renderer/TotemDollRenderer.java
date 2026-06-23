package net.lopymine.mtd.doll.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.AtlasSprite;
import net.lopymine.mtd.bruh.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.rendering.*;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.model.TotemDollModel.Drawer;
import net.lopymine.mtd.doll.renderer.special.*;
import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.utils.*;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;


@ExtensionMethod({ItemStackExtension.class, DrawContextExtension.class})
public class TotemDollRenderer {

	// SUBMIT METHODS

	public static void submitItemAnyway(SubmitNodeCollector collector, PoseStack matrices, DollRenderContext context, ItemStack stack, int light, int overlay, int outlineColor) {
		TotemDollData totemDollData = stack.getTotemDollData(false);
		TotemDollRenderState renderState = new TotemDollRenderState(totemDollData, light, overlay, outlineColor);
		TotemDollRenderer.submitSpecial(collector, matrices, stack.getPlayerEntity(), context, renderState);
	}

	public static boolean submitItem(SubmitNodeCollector collector, PoseStack matrices, DollRenderContext context, ItemStack stack, int light, int overlay, int outlineColor) {
		if (canSubmit(stack)) {
			TotemDollData totemDollData = stack.getTotemDollData(false);
			TotemDollRenderState renderState = new TotemDollRenderState(totemDollData, light, overlay, outlineColor);
			TotemDollRenderer.submitSpecial(collector, matrices, stack.getPlayerEntity(), context, renderState);
			return true;
		}
		return false;
	}

	public static void submitPreview(SubmitNodeCollector collector, PoseStack matrices, float size, @NotNull TotemDollData data) {
		float i = (size / 2F);

		long currentTime = Util.getMillis();
		float rotationSpeed = 0.05f;

		float rotation = (currentTime * rotationSpeed) % 360;

		matrices.pushPose();
		matrices.scale(-i, -i, i);
		matrices.mulPose(Axis.YP.rotationDegrees(rotation));
		matrices.translate(-0.5F, -1.0F, -0.5F);
		TotemDollFeatureRenderer.submit(collector, matrices, new TotemDollRenderState(data));
		matrices.popPose();
	}

	public static void submitSpecial(SubmitNodeCollector collector, PoseStack matrices, AbstractClientPlayer holdingPlayer, DollRenderContext context, TotemDollRenderState renderState) {
		DollRenderContext renderContext = context == DollRenderContext.D_NONE ? DollRenderContext.D_GUI : context;
		TotemDollData data = renderState.data();

		beforeDollSubmit(renderContext, holdingPlayer, data);

		matrices.pushPose();
		renderContext.apply(data.getModelToRender().getMain(), matrices);
		data.getRenderProperties().setRenderContext(renderContext);
		matrices.translate(-0.5F, -1.0F, -0.5F);

		switch (renderContext) {
			case D_FIRST_PERSON_LEFT_HAND,
			     D_FIRST_PERSON_RIGHT_HAND -> TotemDollRenderer.renderInHand(collector, matrices, renderState, renderContext.isLeftHanded(), true);
			case D_THIRD_PERSON_LEFT_HAND,
			     D_THIRD_PERSON_RIGHT_HAND -> TotemDollRenderer.renderInHand(collector, matrices, renderState, renderContext.isLeftHanded(), false);
			default -> TotemDollFeatureRenderer.submit(collector, matrices, renderState);
		}

		matrices.popPose();

		afterDollSubmit();
	}

	// EXTRACT METHODS

	public static void extractPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable TotemDollData data) {
		extractPreview(context, x, y, width, height, size, data, DollRenderContext.D_PREVIEW);
	}

	public static void extractPreview(GuiGraphicsExtractor context, int x, int y, int width, int height, float size, @Nullable TotemDollData data, DollRenderContext renderContext) {
		if (data == null) {
			long currentTime = Util.getMillis();
			float rotationSpeed = 0.05f;
			float rotation = (currentTime * rotationSpeed) % 360;
			context.guiRenderState.addPicturesInPictureState(new ItemGuiRenderState(Items.TOTEM_OF_UNDYING.getDefaultInstance(), x, y, width, height, size, Axis.YP.rotationDegrees(rotation), context.scissorStack.peek()));
		} else {
			data.getRenderProperties().setRenderContext(renderContext);
			context.guiRenderState.addPicturesInPictureState(TotemDollGuiRenderState.getPreview(data, x, y, width, height, size, context.scissorStack.peek()));
		}
	}

	public static void renderInHand(SubmitNodeCollector collector, PoseStack matrices, TotemDollRenderState renderState, boolean leftHanded, boolean firstPerson) {
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

		TotemDollFeatureRenderer.submit(collector, matrices, renderState);

		matrices.popPose();
	}

	// RENDER METHODS

	public static void render(PoseStack matrices, BufferConsumer consumer, int light, int overlay, TotemDollData totemDollData) {
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
		matrices.scale(-1.0F, -1.0F, 1.0F); // - - +
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

		drawer.draw(matrices, consumer, skinSprite, light, overlay, -1);

		matrices.popPose();
	}

	private static void beforeDollSubmit(@Nullable DollRenderContext context, AbstractClientPlayer playerEntity, TotemDollData totemDollData) {
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
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null && !playerEntity.equals(player) && playerEntity.isInvisibleTo(player)) {
				return;
			}
			totemDollData.setFrameSprites(playerEntity);
		}
	}

	private static void afterDollSubmit() {
		ProfilerFiller profiler = ProfilerUtils.getProfiler();
		profiler.pop();
	}

	public static boolean canSubmit(@Nullable ItemStack stack) {
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
