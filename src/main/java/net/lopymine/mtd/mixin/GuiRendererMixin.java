package net.lopymine.mtd.mixin;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.special.*;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@ExtensionMethod(ItemStackExtension.class)
@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

	@Shadow
	@Final
	private GuiRenderState renderState;

	@Shadow @Final private FeatureRenderDispatcher featureRenderDispatcher;

	@Inject(at = @At("HEAD"), method = "preparePictureInPictureState", cancellable = true)
	private void renderDoll(
			PictureInPictureRenderState picturesInPictureState,
			int guiScale,
			//? if fabric {
			CallbackInfo ci
			//?} else {
			/*boolean firstPass,
			CallbackInfoReturnable<Boolean> cir
			*///?}
	) {
		if (!(picturesInPictureState instanceof TotemDollGuiRenderState totemDollGuiRenderState)) {
			return;
		}

		TotemDollData data = totemDollGuiRenderState.data() == null ?
				totemDollGuiRenderState.stack() == null ?
						null
						:
						totemDollGuiRenderState.stack().getTotemDollData()
				:
				totemDollGuiRenderState.data();

		if (data == null) {
			return;
		}

		TotemDollGuiElementRenderer guiRenderer = data.createGuiRenderer();
		guiRenderer.setActive(true);
		guiRenderer.prepare(totemDollGuiRenderState, this.renderState, this.featureRenderDispatcher, guiScale);

		//? if fabric {
		ci.cancel();
		//?} else {
		/*cir.setReturnValue(true);
		*///?}
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
