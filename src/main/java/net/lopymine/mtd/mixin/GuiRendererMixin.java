package net.lopymine.mtd.mixin;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.special.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {


	@Shadow
	@Final
	private BufferSource bufferSource;

	@Shadow @Final private GuiRenderState renderState;

	@Inject(at = @At("HEAD"), method = "preparePictureInPictureState", cancellable = true)
	private void renderDoll(PictureInPictureRenderState elementState, int windowScaleFactor, CallbackInfo ci) {
		if (!(elementState instanceof TotemDollRenderState totemDollRenderState)) {
			return;
		}

		TotemDollData data = totemDollRenderState.data() == null ?
				totemDollRenderState.stack() == null ?
						null
						:
						totemDollRenderState.stack().getTotemDollData()
				:
				totemDollRenderState.data();

		if (data == null) {
			return;
		}

		TotemDollGuiElementRenderer guiRenderer = data.getGuiRenderer(this.bufferSource);
		guiRenderer.setActive(true);
		guiRenderer.prepare(totemDollRenderState, this.renderState, windowScaleFactor);
		ci.cancel();
	}

	@Inject(at = @At(value = "TAIL"), method = "preparePictureInPicture")
	private void clearUnusedRenderers(CallbackInfo ci) {
		TotemDollGuiElementRenderer.clearUnusedRenderers();
	}

	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), method = "close")
	private void closeTotemDollRenderers(CallbackInfo ci) {
		TotemDollGuiElementRenderer.closeTotemRenderers();
	}

}
