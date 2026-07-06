package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.yacl.YACLConfigurationScreen;
import net.lopymine.mtd.yacl.custom.TransparencySprites;

@Mixin(TabButton.class)
public abstract class TabButtonMixin extends AbstractWidget {

	public TabButtonMixin(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	@Shadow
	public abstract boolean isSelected();

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIIIII)V"), method = "renderWidget")
	private void renderTransparencyTab1(GuiGraphics context, ResourceLocation identifier, int x, int y, int w, int h, int a, int b, int c, int d, int e, int k, int l, int u, Operation<Void> original) {
		if (YACLConfigurationScreen.notOpen(Minecraft.getInstance().screen)) {
			original.call(context, identifier, x, y, w, h, a, b, c, d, e, k, l, u);
			return;
		}

		RenderSystem.enableBlend();
		context.blitNineSliced(TransparencySprites.TAB_BUTTON_SPRITES.get(this.isSelected(), this.isHoveredOrFocused()), x, y, this.width, this.height, 2, 130, 24, 0, 0);
		RenderSystem.disableBlend();
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/TabButton;renderFocusUnderline(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"), method = "renderWidget")
	private void renderTabBackground(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		int left = this.getX() + 2;
		int top = this.getY() + 2;
		int right = (this.getX() + this.getWidth()) - 2;
		int bottom = (this.getY() + this.getHeight());

		RenderSystem.enableBlend();
		context.blit(TransparencySprites.getMenuListBackgroundTexture(), left, top, 0, 0, right - left, bottom - top);
		RenderSystem.disableBlend();
	}


}
