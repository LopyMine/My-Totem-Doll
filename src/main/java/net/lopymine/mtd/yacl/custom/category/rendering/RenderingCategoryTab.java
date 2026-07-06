package net.lopymine.mtd.yacl.custom.category.rendering;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.utils.*;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.*;
import dev.isxander.yacl3.gui.tab.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.DrawContextExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.components.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.*;

import net.lopymine.mtd.utils.*;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import net.lopymine.mtd.yacl.custom.screen.MyTotemDollYACLScreen;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(DrawContextExtension.class)
public class RenderingCategoryTab implements TabExt {

	public final Button saveFinishedButton;
	public final Button cancelResetButton;
	public final Button undoButton;
	private final ConfigCategory category;
	private final Tooltip tooltip;
	private final SearchFieldWidget searchField;
	private final ScreenRectangle rightPaneDim;
	public ListHolderWidget<OptionListWidget> optionList;

	public RenderingCategoryTab(YACLScreen screen, ConfigCategory category, ScreenRectangle tabArea) {
		if (!(screen instanceof MyTotemDollYACLScreen yaclScreen)) {
			throw new IllegalArgumentException("This category is only for me! [My Totem Doll]");
		}

		this.category = category;
		this.tooltip  = Tooltip.create(category.tooltip());

		int columnWidth = screen.width / 3;
		int padding = columnWidth / 20;
		columnWidth = Math.min(columnWidth, 400);
		int paddedWidth = columnWidth - padding * 2;
		this.rightPaneDim = new ScreenRectangle(screen.width / 3 * 2, tabArea.top() + 1, screen.width / 3, tabArea.top() + (padding * 2) + 39);
		MutableDimension<Integer> actionDim = Dimension.ofInt(screen.width / 3 * 2 + screen.width / 6, tabArea.top() + padding + 44, paddedWidth, 20);

		this.saveFinishedButton = Button.builder(CommonComponents.GUI_DONE, btn -> yaclScreen.finishOrSave())
				.pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.build();

		actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
		this.cancelResetButton = Button.builder(CommonComponents.GUI_CANCEL, btn -> yaclScreen.cancelOrReset())
				.pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.build();

		actionDim.move(actionDim.width() + 4, 0);
		this.undoButton = Button.builder(Component.translatable("yacl.gui.undo"), btn -> yaclScreen.undo())
				.pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.tooltip(Tooltip.create(Component.translatable("yacl.gui.undo.tooltip")))
				.build();

		this.searchField = new SearchFieldWidget(
				screen,
				Minecraft.getInstance().font,
				screen.width / 3 * 2 + screen.width / 6 - paddedWidth / 2 + 1,
				this.undoButton.getY() - 22,
				paddedWidth - 2, 18,
				Component.translatable("gui.recipebook.search_hint"),
				Component.translatable("gui.recipebook.search_hint"),
				searchQuery -> optionList.getList().updateSearchQuery(searchQuery)
		);

		this.optionList = new ListHolderWidget<>(
				() -> new ScreenRectangle(tabArea.position(), tabArea.width() / 3 * 2 - 2, tabArea.height()),
				new OptionListWidget(screen, category, screen.minecraft, 0, 0, screen.width / 3 * 2 + 1, screen.height, desc -> {})
		);

		updateButtons();
	}

	@Override
	public Component getTabTitle() {
		return this.category.name();
	}

	@Override
	public void visitChildren(Consumer<AbstractWidget> consumer) {
		consumer.accept(this.optionList);
		consumer.accept(this.saveFinishedButton);
		consumer.accept(this.cancelResetButton);
		consumer.accept(this.undoButton);
		consumer.accept(this.searchField);
	}

	@Override
	public void renderBackground(GuiGraphics context) {
		RenderUtils.enableBlend();
		RenderUtils.enableDepthTest();

		// right pane darker db
		DrawUtils.drawTexture(context, TransparencySprites.getMenuListBackgroundTexture(), this.rightPaneDim.left(), this.rightPaneDim.top(), this.rightPaneDim.right() + 2, this.rightPaneDim.bottom() + 2, this.rightPaneDim.width() + 2, this.rightPaneDim.height() + 2, 32, 32);

		// top separator for right pane
		context.push();
		context.translate(0, 0, 10);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), this.rightPaneDim.left() - 1, this.rightPaneDim.top() - 2, 0.0F, 0.0F, this.rightPaneDim.width() + 1, 2, 32, 2);
		context.pop();

		// down separator for bottom pane
		context.push();
		context.translate(this.rightPaneDim.right() + 1, this.rightPaneDim.bottom() + 2, 0);
		context.rotateZ(180);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), 0, 0, 0.0F, 0.0F, this.rightPaneDim.width() + 2, 2, 32, 2);
		context.pop();

		// left separator for right pane
		context.push();
		context.translate(this.rightPaneDim.left(), this.rightPaneDim.top() - 1, 0);
		context.rotateZ(90);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), 0, 0, 0f, 0f, this.rightPaneDim.height() + 2, 2, 32, 2);
		context.pop();

		RenderUtils.disableBlend();
		RenderUtils.disableDepthTest();
	}


	@Override
	public void doLayout(ScreenRectangle area) {
	}

	@Nullable
	@Override
	public Tooltip getTooltip() {
		return this.tooltip;
	}

	public void updateButtons() {
		this.undoButton.active = false;
		this.saveFinishedButton.setMessage(CommonComponents.GUI_DONE);
		this.saveFinishedButton.setTooltip(Tooltip.create(Component.translatable("yacl.gui.finished.tooltip")));
		this.cancelResetButton.setMessage(Component.translatable("controls.reset"));
		this.cancelResetButton.setTooltip(Tooltip.create(Component.translatable("yacl.gui.reset.tooltip")));
	}
}
