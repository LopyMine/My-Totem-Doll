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

@Mixin(ItemCombinerScreen.class)
public class ItemCombinerScreenMixin {

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"
			),
			method = "extractBackground"
	)
	private void drawBackground(GuiGraphicsExtractor instance, com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		Consumer<Integer> draw = (w) -> original.call(instance, renderPipeline, identifier, x, y, u, v, w, height, textureWidth, textureHeight);
		this.drawBackground(width, draw);
	}

	@Unique
	private void drawBackground(int width, Consumer<Integer> draw) {
		if (this instanceof MTDAnvilScreen && MyTotemDollConfig.getInstance().isModEnabled()) {
			draw.accept(176);
			return;
		}
		draw.accept(width);
	}

}