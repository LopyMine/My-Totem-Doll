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
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@Getter
@Setter
@ExtensionMethod(ItemStackExtension.class)
public class TotemDollGuiElementRenderer extends PictureInPictureRenderer<TotemDollGuiRenderState> {

	public static final Map<TotemDollRenderProperties, TotemDollGuiElementRenderer> PROPERTIES_RENDERERS = new HashMap<>();

	private boolean active;

	@NotNull
	public static TotemDollGuiElementRenderer createGuiRenderer(TotemDollRenderProperties renderProperties) {
		TotemDollGuiElementRenderer renderer = PROPERTIES_RENDERERS.get(renderProperties.copy());
		if (renderer == null) {
			TotemDollGuiElementRenderer createdRenderer = new TotemDollGuiElementRenderer();
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
	protected void renderToTexture(TotemDollGuiRenderState state, PoseStack matrices, SubmitNodeCollector collector) {
		LightningUtils.flat();

		if (state.renderContext() == DollRenderContext.D_PREVIEW && state.data() != null) {
			TotemDollRenderer.submitPreview(collector, matrices, state.size() + 1, state.data());
		} else if (state.stack() != null) {
			matrices.pushPose();
			matrices.scale(16F, -16F, -16F);
			TotemDollRenderer.submitItem(collector, matrices, state.renderContext(), state.stack(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
			matrices.popPose();

			if (state.stack().hasModdedModel()) {
				state.stack().setModdedModel(false);
			}
		}
	}

	@NonNull
	@Override
	public Class<TotemDollGuiRenderState> getRenderStateClass() {
		return TotemDollGuiRenderState.class;
	}

	@NonNull
	@Override
	protected String getTextureLabel() {
		return "%s-doll-special-gui-renderer".formatted(MyTotemDoll.MOD_ID);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2F;
	}
}
