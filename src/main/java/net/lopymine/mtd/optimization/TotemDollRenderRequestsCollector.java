package net.lopymine.mtd.optimization;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.LockableAtlasTexture;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.MatrixStackEntryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(MatrixStackEntryExtension.class)
public class TotemDollRenderRequestsCollector {

	private static final TotemDollRenderRequestsCollector INSTANCE = new TotemDollRenderRequestsCollector();
	private final PoseStack matrices = new PoseStack();
	private final List<TotemDollRenderRequest> requests = new ArrayList<>();
	private final TotemDollRenderProperties tempProperties = new TotemDollRenderProperties();

	private TotemDollRenderRequestsCollector() {

	}

	public static TotemDollRenderRequestsCollector getInstance() {
		return INSTANCE;
	}

	public void requestRender(PoseStack matrices, TotemDollData data, AbstractClientPlayer holdingPlayer, DollRenderContext context, int light, int overlay, int outlineColor, @Nullable MultiBufferSource provider) {
		PoseStack.Pose entry = matrices.last();
		this.requests.add(new TotemDollRenderRequest(entry.copy(), data, data.getRenderProperties().copy(), holdingPlayer, context, light, overlay, outlineColor, provider));
	}

	public void renderStates() {
		LockableAtlasTexture atlasTexture = MyTotemDollAtlasManager.getNullableAtlasTexture();
		if (atlasTexture == null) {
			MyTotemDollClient.LOGGER.error("Game tried to render doll model requests, but atlas not initialized yet!");
			return;
		}
		atlasTexture.setLocked(true);

		BufferSource mainProvider = Minecraft.getInstance().renderBuffers().bufferSource();
		OutlineBufferSource outlineProvider = Minecraft.getInstance().renderBuffers().outlineBufferSource();

		for (TotemDollRenderRequest request : this.requests) {
			this.renderRequest(request, request.provider() == null ? mainProvider : request.provider(), outlineProvider);
		}

		this.requests.clear();
		mainProvider.endLastBatch();
		// We should draw this before unlocking, to make sure that atlas won't be changed earlier than the draw call
		atlasTexture.setLocked(false);
	}

	private void renderRequest(TotemDollRenderRequest request, MultiBufferSource mainProvider, @SuppressWarnings("unused") OutlineBufferSource outlineProvider) {
		this.matrices.pushPose();
		this.matrices.last().copyFrom(request.copyPeek());

		TotemDollData data = request.data();
		this.tempProperties.copyFrom(data.getRenderProperties());

		data.getRenderProperties().copyFrom(request.renderProperties());
		data.clearFrameModel();
		TotemDollModel modelToRender = data.getModelToRender();
		modelToRender.resetPartsVisibility();
		data.getRenderProperties().applyToModel(modelToRender);

		TotemDollRenderer.renderDoll(this.matrices, data, request.holdingPlayer(), request.context(), mainProvider, request.light(), request.overlay());

		int argb = request.outlineColor();
		if (argb != 0) {
			outlineProvider.setColor(argb);
			TotemDollRenderer.renderDoll(this.matrices, data, request.holdingPlayer(), request.context(), outlineProvider, request.light(), request.overlay());
		}

		data.getRenderProperties().copyFrom(this.tempProperties);

		this.matrices.popPose();
	}

}
