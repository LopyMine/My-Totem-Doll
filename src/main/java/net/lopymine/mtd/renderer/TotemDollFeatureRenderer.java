package net.lopymine.mtd.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.LockableAtlasTexture;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.MatrixStackEntryExtension;
import net.lopymine.mtd.utils.mixin.MyTotemDollSubmitNodeCollection;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

@ExtensionMethod(MatrixStackEntryExtension.class)
public class TotemDollFeatureRenderer {

	private final PoseStack matrices = new PoseStack();
	private final TotemDollRenderProperties tempProperties = new TotemDollRenderProperties();

	public static void submit(SubmitNodeCollector collector, PoseStack matrices, TotemDollData data, int light, int overlay, int outlineColor) {
		if (!(collector instanceof SubmitNodeStorage storage)) {
			return;
		}
		SubmitNodeCollection collection = storage.order(0);
		if (!(collection instanceof MyTotemDollSubmitNodeCollection modCollection)) {
			return;
		}
		PoseStack.Pose entry = matrices.last();
		modCollection.myTotemDoll$submit(new TotemDollFeature(entry.copy(), data, data.getRenderProperties().copy(), light, overlay, outlineColor));
	}

	public void render(SubmitNodeCollection collection, BufferSource mainProvider, OutlineBufferSource outlineProvider) {
		if (!(collection instanceof MyTotemDollSubmitNodeCollection modCollection)) {
			return;
		}
		LockableAtlasTexture atlasTexture = MyTotemDollAtlasManager.getNullableAtlasTexture();
		if (atlasTexture == null) {
			MyTotemDollClient.LOGGER.error("Game tried to render doll model requests, but atlas not initialized yet!");
			return;
		}
		atlasTexture.setLocked(true);

		for (TotemDollFeature request : modCollection.myTotemDoll$getFeatures()) {
			this.renderRequest(request, mainProvider, outlineProvider);
		}

		mainProvider.endLastBatch();
		// We should draw this before unlocking, to make sure that atlas won't be changed earlier than the draw call
		atlasTexture.setLocked(false);
	}

	private void renderRequest(TotemDollFeature feature, MultiBufferSource mainProvider, @SuppressWarnings("unused") OutlineBufferSource outlineProvider) {
		this.matrices.pushPose();
		this.matrices.last().copyFrom(feature.copyPeek());

		TotemDollData data = feature.data();
		this.tempProperties.copyFrom(data.getRenderProperties());

		data.getRenderProperties().copyFrom(feature.renderProperties());
		data.clearFrameModel();
		TotemDollModel modelToRender = data.getModelToRender();
		modelToRender.resetPartsVisibility();
		data.getRenderProperties().applyToModel(modelToRender);

		TotemDollRenderer.render(this.matrices, mainProvider, feature.light(), feature.overlay(), feature.data());

		int argb = feature.outline();
		if (argb != 0) {
			outlineProvider.setColor(argb);
			TotemDollRenderer.render(this.matrices, mainProvider, feature.light(), feature.overlay(), feature.data());
		}

		data.getRenderProperties().copyFrom(this.tempProperties);

		this.matrices.popPose();
	}

}
