package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import java.util.function.Consumer;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

//? if >=26.3 {
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
//?} else {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?}

@Mixin(ItemCombinerScreen.class)
public class ItemCombinerScreenMixin {

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					//? if >=26.3 {
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
					//?} else {
					/*target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
					*///?}
			),
			method = "extractBackground"
	)
	private void drawBackground(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		Consumer<Integer> draw = (w) -> original.call(instance, renderPipeline, identifier, x, y, u, v, w, height, textureWidth, textureHeight);
		this.myTotemDoll$drawBackground(width, draw);
	}

	@Unique
	private void myTotemDoll$drawBackground(int width, Consumer<Integer> draw) {
		if (this instanceof MTDAnvilScreen && MyTotemDollConfig.getInstance().isModEnabled()) {
			draw.accept(176);
			return;
		}
		draw.accept(width);
	}

}