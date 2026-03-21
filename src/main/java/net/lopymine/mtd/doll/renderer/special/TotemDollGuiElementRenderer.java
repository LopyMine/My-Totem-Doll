package net.lopymine.mtd.doll.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollRenderProperties;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.utils.LightningUtils;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ExtensionMethod(ItemStackExtension.class)
public class TotemDollGuiElementRenderer extends PictureInPictureRenderer<TotemDollRenderState> {

	public static final Map<TotemDollRenderProperties, TotemDollGuiElementRenderer> PROPERTIES_RENDERERS = new HashMap<>();

	private boolean active;

	public TotemDollGuiElementRenderer(BufferSource vertexConsumers) {
		super(vertexConsumers);
	}

	@NotNull
	public static TotemDollGuiElementRenderer getRenderer(TotemDollRenderProperties renderProperties, BufferSource immediate) {
		TotemDollGuiElementRenderer renderer = PROPERTIES_RENDERERS.get(renderProperties.copy());
		if (renderer == null) {
			TotemDollGuiElementRenderer createdRenderer = new TotemDollGuiElementRenderer(immediate);
			PROPERTIES_RENDERERS.put(renderProperties, createdRenderer);
			return createdRenderer;
		}

		return renderer;
	}

	public static void closeTotemRenderers() {
		PROPERTIES_RENDERERS.values().forEach(TotemDollGuiElementRenderer::close);
	}

	public static void clearUnusedRenderers() {
		int all = PROPERTIES_RENDERERS.size();
		PROPERTIES_RENDERERS.entrySet().removeIf((entry) -> {
			TotemDollGuiElementRenderer renderer = entry.getValue();
			if (!renderer.isActive()) {
				renderer.close();
				return true;
			}
			renderer.setActive(false);
			return false;
		});
		int cleared = all - PROPERTIES_RENDERERS.size();
		if (MyTotemDollConfig.getInstance().isDebugLogEnabled() && cleared != 0) {
			MyTotemDollClient.LOGGER.info("Removed Inactive Totem Doll Renderers: {}", cleared);
		}
	}

	@Override
	protected void renderToTexture(TotemDollRenderState state, PoseStack matrices) {
		if (state.renderContext() == DollRenderContext.D_PREVIEW && state.data() != null) {
			TotemDollRenderer.renderDataPreview(matrices, this.bufferSource, this.bufferSource::endBatch, state.size() + 1, state.data());
		} else if (state.stack() != null) {
			LightningUtils.disable3dLighting();
			matrices.pushPose();
			matrices.scale(16F, -16F, -16F);
			TotemDollRenderer.renderDoll(matrices, state.stack(), state.renderContext(), this.bufferSource, 15728880, OverlayTexture.NO_OVERLAY);
			matrices.popPose();
			this.bufferSource.endBatch();
			LightningUtils.enable3dLighting();

			if (state.stack().hasModdedModel()) {
				state.stack().setModdedModel(false);
			}
		}
	}

	@Override
	public Class<TotemDollRenderState> getRenderStateClass() {
		return TotemDollRenderState.class;
	}

	@Override
	protected String getTextureLabel() {
		return "%s-doll-special-gui-renderer".formatted(MyTotemDoll.MOD_ID);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
	}
}
