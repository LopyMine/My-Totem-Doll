package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import java.util.function.Consumer;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ItemCombinerScreen.class)
public class ItemCombinerScreenMixin {

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
			),
			method = "renderBg"
	)
	private void drawBackground(GuiGraphics instance, ResourceLocation texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
		Consumer<Integer> draw = (w) -> original.call(instance, texture, x, y, u, v, w, height);
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