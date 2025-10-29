package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.function.*;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;

@Mixin(ForgingScreen.class)
public class ForgingScreenMixin {

	//? if >=1.21.6 {
	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIII)V"
			),
			method = "drawBackground"
	)
	private void drawBackground(DrawContext instance, com.mojang.blaze3d.pipeline.RenderPipeline renderPipeline, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		Consumer<Integer> draw = (w) -> original.call(instance, renderPipeline, identifier, x, y, u, v, w, height, textureWidth, textureHeight);
		this.drawBackgroundd(width, draw);
	}
	//?} elif >=1.21.2 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIII)V"), method = "drawBackground")
	private void drawBackground(DrawContext instance, Function<?, ?> function, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		if (this instanceof MTDAnvilScdddreen && MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, function, identifier, x, y, u, v, 176, height, textureWidth, textureHeight);
			return;
		}
		original.call(instance, function, identifier, x, y, u, v, width, height, textureWidth, textureHeight);
	}
	*///?} elif <=1.21.1 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"), method = "drawBackground")
	private void drawBackground(DrawContext instance, Identifier texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
		if (this instanceof MTDAnvilScrddddeen && MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, texture, x, y, u, v, 176, height);
			return;
		}
		original.call(instance, texture, x, y, u, v, width, height);
	}
	*///?}

	@Unique
	private void drawBackgroundd(int width, Consumer<Integer> draw) {
		if (this instanceof MTDAnvilScreen && MyTotemDollConfig.getInstance().isModEnabled()) {
			draw.accept(176);
			return;
		}
		draw.accept(width);
	}

}