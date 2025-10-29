package net.lopymine.mtd.yacl.custom.category.rendering;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.utils.*;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.*;
import dev.isxander.yacl3.gui.tab.*;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.DrawContextExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.math.RotationAxis;

import com.mojang.blaze3d.systems.RenderSystem;

import net.lopymine.mtd.utils.*;
import net.lopymine.mtd.yacl.custom.TransparencySprites;
import net.lopymine.mtd.yacl.custom.screen.MyTotemDollYACLScreen;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

@ExtensionMethod(DrawContextExtension.class)
public class RenderingCategoryTab implements TabExt {

	public final ButtonWidget saveFinishedButton;
	public final ButtonWidget cancelResetButton;
	public final ButtonWidget undoButton;
	private final ConfigCategory category;
	private final Tooltip tooltip;
	private final SearchFieldWidget searchField;
	private final ScreenRect rightPaneDim;
	//? if >=1.21.9 {
	private WidgetAndType<OptionListWidget> optionList;
	//?} else {
	/*public ListHolderWidget<OptionListWidget> optionList;
	*///?}

	public RenderingCategoryTab(YACLScreen screen, ConfigCategory category, ScreenRect tabArea) {
		if (!(screen instanceof MyTotemDollYACLScreen yaclScreen)) {
			throw new IllegalArgumentException("This category is only for me! [My Totem Doll]");
		}

		this.category = category;
		this.tooltip  = Tooltip.of(category.tooltip());

		int columnWidth = screen.width / 3;
		int padding = columnWidth / 20;
		columnWidth = Math.min(columnWidth, 400);
		int paddedWidth = columnWidth - padding * 2;
		this.rightPaneDim = new ScreenRect(screen.width / 3 * 2, tabArea.getTop() + 1, screen.width / 3, tabArea.getTop() + (padding * 2) + 39);
		MutableDimension<Integer> actionDim = Dimension.ofInt(screen.width / 3 * 2 + screen.width / 6, tabArea.getTop() + padding + 44, paddedWidth, 20);

		this.saveFinishedButton = ButtonWidget.builder(ScreenTexts.DONE, btn -> yaclScreen.finishOrSave())
				.position(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.build();

		actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
		this.cancelResetButton = ButtonWidget.builder(ScreenTexts.CANCEL, btn -> yaclScreen.cancelOrReset())
				.position(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.build();

		actionDim.move(actionDim.width() + 4, 0);
		this.undoButton = ButtonWidget.builder(Text.translatable("yacl.gui.undo"), btn -> yaclScreen.undo())
				.position(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.tooltip(Tooltip.of(Text.translatable("yacl.gui.undo.tooltip")))
				.build();

		this.searchField = new SearchFieldWidget(
				screen,
				MinecraftClient.getInstance().textRenderer,
				screen.width / 3 * 2 + screen.width / 6 - paddedWidth / 2 + 1,
				this.undoButton.getY() - 22,
				paddedWidth - 2, 18,
				Text.translatable("gui.recipebook.search_hint"),
				Text.translatable("gui.recipebook.search_hint"),
				//? if >=1.21.9 {
				(searchQuery) -> this.optionList.getType().updateSearchQuery(searchQuery)
				//?} else {
				/*searchQuery -> optionList.getList().updateSearchQuery(searchQuery)
				*///?}
		);

		//? if >=1.21.9 {
		this.optionList = YACLSelectionList.asWidget(new OptionListWidget(
				screen,
				category,
				MinecraftClient.getInstance(),
				0,
				0,
				screen.width / 3 * 2 + 1,
				screen.height,
				(desc) -> {}
		));
		//?} else {
		/*this.optionList = new ListHolderWidget<>(
				() -> new ScreenRect(tabArea.position(), tabArea.width() / 3 * 2 - 2, tabArea.height()),
				new OptionListWidget(screen, category, screen.client, 0, 0, screen.width / 3 * 2 + 1, screen.height, desc -> {})
		);
		*///?}

		updateButtons();
	}

	@Override
	public Text getTitle() {
		return this.category.name();
	}

	@Override
	public void forEachChild(Consumer<ClickableWidget> consumer) {
		consumer.accept(this.optionList/*? if >=1.21.9 {*/.getWidget() /*?}*/);
		consumer.accept(this.saveFinishedButton);
		consumer.accept(this.cancelResetButton);
		consumer.accept(this.undoButton);
		consumer.accept(this.searchField);
	}


	@Override
	public void renderBackground(DrawContext context) {
		RenderUtils.enableBlend();
		RenderUtils.enableDepthTest();

		// right pane darker db
		DrawUtils.drawTexture(context, TransparencySprites.getMenuListBackgroundTexture(), this.rightPaneDim.getLeft(), this.rightPaneDim.getTop(), this.rightPaneDim.getRight() + 2, this.rightPaneDim.getBottom() + 2, this.rightPaneDim.width() + 2, this.rightPaneDim.height() + 2, 32, 32);

		// top separator for right pane
		context.push();
		context.translate(0, 0, 10);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), this.rightPaneDim.getLeft() - 1, this.rightPaneDim.getTop() - 2, 0.0F, 0.0F, this.rightPaneDim.width() + 1, 2, 32, 2);
		context.pop();

		// down separator for bottom pane
		context.push();
		context.translate(this.rightPaneDim.getRight() + 1, this.rightPaneDim.getBottom() + 2, 0);
		context.rotateZ(180);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), 0, 0, 0.0F, 0.0F, this.rightPaneDim.width() + 2, 2, 32, 2);
		context.pop();

		// left separator for right pane
		context.push();
		context.translate(this.rightPaneDim.getLeft(), this.rightPaneDim.getTop() - 1, 0);
		context.rotateZ(90);
		DrawUtils.drawTexture(context, TransparencySprites.getMenuSeparatorTexture(), 0, 0, 0f, 0f, this.rightPaneDim.height() + 2, 2, 32, 2);
		context.pop();

		RenderUtils.disableBlend();
		RenderUtils.disableDepthTest();
	}


	@Override
	public void refreshGrid(ScreenRect area) {
		//? if >=1.21.9 {
		ScreenRect rect = new ScreenRect(area.position(), area.width() / 3 * 2, area.height());
		this.optionList.getType().setX(rect.getLeft());
		this.optionList.getType().setY(rect.getTop() + 1);
		this.optionList.getType().setWidth(rect.width());
		this.optionList.getType().setHeight(rect.height() - 1);
		//?}
	}

	@Nullable
	@Override
	public Tooltip getTooltip() {
		return tooltip;
	}

	public void updateButtons() {
		this.undoButton.active = false;
		this.saveFinishedButton.setMessage(ScreenTexts.DONE);
		this.saveFinishedButton.setTooltip(Tooltip.of(Text.translatable("yacl.gui.finished.tooltip")));
		this.cancelResetButton.setMessage(Text.translatable("controls.reset"));
		this.cancelResetButton.setTooltip(Tooltip.of(Text.translatable("yacl.gui.reset.tooltip")));
	}
}
