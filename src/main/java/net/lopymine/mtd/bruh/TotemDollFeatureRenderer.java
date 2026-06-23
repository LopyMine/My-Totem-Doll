package net.lopymine.mtd.bruh;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import net.fabricmc.api.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.LockableAtlasTexture;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.mixin.bruh.SubmitNodeCollectionAccessor;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.submit.*;
import org.jspecify.annotations.*;

public class TotemDollFeatureRenderer extends RenderTypeFeatureRenderer<TotemDollFeatureRenderer.Submit> {

	public static final FeatureRendererType<TotemDollFeatureRenderer.Submit> TYPE = FeatureRendererType.create(MyTotemDoll.MOD_NAME);
	private final PoseStack matrices = new PoseStack();
	private final TotemDollRenderProperties renderProperties = new TotemDollRenderProperties();

	public static void submit(
			SubmitNodeCollector collector,
			PoseStack matrices,
			TotemDollRenderState renderState
	) {
		if (!(collector instanceof SubmitNodeStorage storage)) {
			return;
		}
		SubmitNodeCollection collection = storage.order(0);
		if (!(collection instanceof SubmitNodeCollectionAccessor accessor)) {
			return;
		}
		PoseStack.Pose entry = matrices.last();
		TotemDollData data = renderState.data();
		Submit submit = new Submit(entry.copy(), data, data.getRenderProperties().copy(), renderState.light(), renderState.overlay(), renderState.overlay());
		accessor.getTranslucentModels().submit(submit);
	}

	@Override
	protected void buildGroup(@NonNull FeatureFrameContext context, @NonNull List<Submit> submits) {
		LockableAtlasTexture atlasTexture = MyTotemDollAtlasManager.getNullableAtlasTexture();
		if (atlasTexture == null) {
			MyTotemDollClient.LOGGER.error("Game tried to render doll model requests, but atlas not initialized yet!");
			return;
		}

		atlasTexture.setLocked(true);

		for (Submit submit : submits) {
			this.renderSubmit(submit);
		}

		// We should draw this before unlocking, to make sure that atlas won't be changed earlier than the draw call
		atlasTexture.setLocked(false);
	}

	private void renderSubmit(Submit submit) {
		this.matrices.pushPose();
		this.matrices.last().set(submit.copyPeek());

		TotemDollData data = submit.data();
		this.renderProperties.copyFrom(data.getRenderProperties());

		data.getRenderProperties().copyFrom(submit.renderProperties());
		data.clearFrameModel();
		TotemDollModel modelToRender = data.getModelToRender();
		modelToRender.resetPartsVisibility();
		data.getRenderProperties().applyToModel(modelToRender);

		TotemDollRenderer.render(this.matrices, this::getVertexBuilder, submit.light(), submit.overlay(), data);

//		int argb = submit.outline();
//		if (argb != 0) {
//			TotemDollRenderer.renderDoll(this.matrices, data, submit.holdingPlayer(), submit.context(), outlineProvider, submit.light(), submit.overlay());
//		}

		data.getRenderProperties().copyFrom(this.renderProperties);

		this.matrices.popPose();
	}

	@Environment(EnvType.CLIENT)
	public record Submit(
			Pose copyPeek,
			TotemDollData data,
			TotemDollRenderProperties renderProperties,
			int light,
			int overlay,
			int outlineColor
	) implements TranslucentSubmit {

		@Override
		public float distanceToCameraSq() {
			return TranslucentSubmit.computeDistanceToCameraSq(this.copyPeek.pose());
		}

		@Override
		public @NonNull FeatureRendererType<? extends TranslucentSubmit> featureType() {
			return TYPE;
		}
	}

}
