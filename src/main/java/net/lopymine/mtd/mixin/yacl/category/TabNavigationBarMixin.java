package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.TransparencySprites;

@Mixin(TabNavigationBar.class)
public class TabNavigationBarMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"), method = "render")
	private void renderTransparencyHeaderSeparator(GuiGraphics context, ResourceLocation textureId, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(context, textureId, x, y, u, v, width, height, textureWidth, textureHeight);
			return;
		}
		RenderSystem.enableBlend();
		context.blit(TransparencySprites.getMenuSeparatorTexture(), x, y, u, x, width, height, textureWidth, textureHeight);
		RenderSystem.disableBlend();
	}

	@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"), method = "render")
	private boolean disableBlackBackground(GuiGraphics instance, int x1, int y1, int x2, int y2, int color) {
		return YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen);
	}

}
