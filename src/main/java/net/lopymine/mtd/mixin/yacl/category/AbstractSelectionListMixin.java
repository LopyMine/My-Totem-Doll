package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import java.util.function.Function;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin {

	@Shadow
	protected int y0;
	@Shadow
	protected int width;
	@Shadow
	protected int height;

	@Shadow
	protected abstract int getScrollbarPosition();

	@ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;renderBackground:Z", opcode = Opcodes.GETFIELD), method = "render")
	private boolean disableBackgroundRendering(boolean original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			return original;
		}
		return false;
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 0), method = "render")
	private void renderTransparencyScrollerBackground1(GuiGraphics context, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(context, x1, y1, x2, y2, color);
			return;
		}
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		context.blitNineSliced(TransparencySprites.SCROLLER_BACKGROUND_SPRITE, this.getScrollbarPosition(), this.y0, 6, this.height, 1, 6, 32, 0, 0);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 1), method = "render")
	private void renderTransparencyScroller2(GuiGraphics context, int x, int y, int width, int height, int color, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(context, x, y, width, height, color);
			return;
		}

		context.blitNineSliced(TransparencySprites.SCROLLER_SPRITE, this.getScrollbarPosition(), y, 6, height - y, 1, 6, 32, 0, 0);
		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}

	@WrapWithCondition(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V", ordinal = 2), method = "render")
	private boolean renderTransparencyScrollerBackground3(GuiGraphics instance, int x1, int y1, int x2, int y2, int color) {
		return YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen);
	}

	@ModifyExpressionValue(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;renderTopAndBottom:Z", opcode = Opcodes.GETFIELD), method = "render")
	private boolean disableShadows(boolean original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			return original;
		}
		return false;
	}

}
