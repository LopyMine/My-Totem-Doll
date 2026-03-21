package net.lopymine.mtd.yacl.custom.category.better;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.YACLScreen.CategoryTab;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.DrawContextExtension;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.*;

@ExtensionMethod(DrawContextExtension.class)
public class BetterCategoryTab extends CategoryTab {

	private final ScreenRectangle rightPaneDim;

	public BetterCategoryTab(YACLScreen screen, ConfigCategory category, ScreenRectangle tabArea) {
		super(screen, category, tabArea);
		this.rightPaneDim = new ScreenRectangle(screen.width / 3 * 2, tabArea.top() + 1, screen.width / 3, tabArea.height());
	}

	@Override
	public void renderBackground(GuiGraphicsExtractor context) {
		// right pane darker db
		DrawUtils.drawTexture(context, TransparencySprites.getMenuListBackgroundTexture(), rightPaneDim.left(), rightPaneDim.top(), rightPaneDim.right() + 2, rightPaneDim.bottom() + 2, rightPaneDim.width() + 2, rightPaneDim.height() + 2, 32, 32);

		// top separator for right pane
		context.push();
		context.translate(0, 0, 10);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), rightPaneDim.left() - 1, rightPaneDim.top() - 2, 0.0F, 0.0F, rightPaneDim.width() + 1, 2, 32, 2);
		context.pop();

		// left separator for right pane
		context.push();
		context.translate(rightPaneDim.left(), rightPaneDim.top() - 1, 0);
		context.rotateZ(90);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), 0, 0, 0f, 0f, rightPaneDim.height() + 1, 2, 32, 2);
		context.pop();
	}

	@Override
	public void updateButtons() {
		this.undoButton.active = false;
		this.saveFinishedButton.setMessage(CommonComponents.GUI_DONE);
		this.saveFinishedButton.setTooltip(Tooltip.create(Component.translatable("yacl.gui.finished.tooltip")));
		this.cancelResetButton.setMessage(Component.translatable("controls.reset"));
		this.cancelResetButton.setTooltip(Tooltip.create(Component.translatable("yacl.gui.reset.tooltip")));
	}
}
