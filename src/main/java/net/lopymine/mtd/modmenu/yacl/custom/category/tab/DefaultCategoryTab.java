package net.lopymine.mtd.modmenu.yacl.custom.category.tab;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.*;
import net.minecraft.util.math.RotationAxis;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.modmenu.yacl.TransparencySprites;

public class DefaultCategoryTab extends /*? if =1.20.2 || =1.20.3 || =1.20 {*/ /*net.lopymine.mtd.modmenu.yacl.custom.category.dummy.DummyCategoryTabImpl *//*?} else {*/ dev.isxander.yacl3.gui.YACLScreen.CategoryTab /*?}*/ {

	private final ScreenRect rightPaneDim;

	public DefaultCategoryTab(YACLScreen screen, ConfigCategory category, ScreenRect tabArea) {
		super(screen, category, tabArea);
		this.rightPaneDim = new ScreenRect(screen.width / 3 * 2, tabArea.getTop() + 1, screen.width / 3, tabArea.height());
	}

	@Override
	public void renderBackground(DrawContext graphics) {
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		// right pane darker db
		graphics.drawTexture(TransparencySprites.MENU_LIST_BACKGROUND_TEXTURE, rightPaneDim.getLeft(), rightPaneDim.getTop(), rightPaneDim.getRight() + 2, rightPaneDim.getBottom() + 2, rightPaneDim.width() + 2, rightPaneDim.height() + 2, 32, 32);

		// top separator for right pane
		graphics.getMatrices().push();
		graphics.getMatrices().translate(0, 0, 10);
		graphics.drawTexture(TransparencySprites.HEADER_SEPARATOR_TEXTURE, rightPaneDim.getLeft() - 1, rightPaneDim.getTop() - 2, 0.0F, 0.0F, rightPaneDim.width() + 1, 2, 32, 2);
		graphics.getMatrices().pop();

		// left separator for right pane
		graphics.getMatrices().push();
		graphics.getMatrices().translate(rightPaneDim.getLeft(), rightPaneDim.getTop() - 1, 0);
		graphics.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90), 0, 0, 1);
		graphics.drawTexture(TransparencySprites.FOOTER_SEPARATOR_TEXTURE, 0, 0, 0f, 0f, rightPaneDim.height() + 1, 2, 32, 2);
		graphics.getMatrices().pop();

		RenderSystem.disableBlend();
		RenderSystem.disableDepthTest();
	}
}
