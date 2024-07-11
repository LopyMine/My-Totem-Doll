package net.lopymine.mtd.modmenu.yacl.custom.category.dummy;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.*;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.*;
import dev.isxander.yacl3.gui.YACLScreen.CategoryTab;
import dev.isxander.yacl3.gui.tab.*;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;

import net.lopymine.mtd.modmenu.yacl.custom.screen.MyTotemDollYACLScreen;

import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public abstract class DummyCategoryTabImpl implements TabExt, DummyCategoryTab {

	public final ButtonWidget saveFinishedButton;
	private final ConfigCategory category;
	private final Tooltip tooltip;
	private final ButtonWidget cancelResetButton;
	private final ButtonWidget undoButton;
	private final SearchFieldWidget searchField;
	private ListHolderWidget<OptionListWidget> optionList;
	private OptionDescriptionWidget descriptionWidget;

	public DummyCategoryTabImpl(YACLScreen screen, ConfigCategory category, ScreenRect tabArea) {
		if (!(screen instanceof MyTotemDollYACLScreen yaclScreen)) {
			throw new IllegalArgumentException("This category only for me! [My Totem Doll]");
		}

		this.category = category;
		this.tooltip  = Tooltip.of(category.tooltip());

		int columnWidth = screen.width / 3;
		int padding = columnWidth / 20;
		columnWidth = Math.min(columnWidth, 400);
		int paddedWidth = columnWidth - padding * 2;
		MutableDimension<Integer> actionDim = Dimension.ofInt(screen.width / 3 * 2 + screen.width / 6, screen.height - padding - 20, paddedWidth, 20);

		this.saveFinishedButton = ButtonWidget.builder(Text.literal("Done"), btn -> yaclScreen.finishOrSave())
				.position(actionDim.x() - actionDim.width() / 2, actionDim.y())
				.size(actionDim.width(), actionDim.height())
				.build();

		actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
		this.cancelResetButton = ButtonWidget.builder(Text.literal("Cancel"), btn -> yaclScreen.cancelOrReset())
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
				screen.textRenderer,
				screen.width / 3 * 2 + screen.width / 6 - paddedWidth / 2 + 1,
				this.undoButton.getY() - 22,
				paddedWidth - 2, 18,
				Text.translatable("gui.recipebook.search_hint"),
				Text.translatable("gui.recipebook.search_hint"),
				searchQuery -> this.optionList.getList().updateSearchQuery(searchQuery)
		);

		this.optionList = new ListHolderWidget<>(
				() -> new ScreenRect(tabArea.position(), tabArea.width() / 3 * 2 + 1, tabArea.height()),
				new OptionListWidget(screen, category, screen.client, 0, 0, screen.width / 3 * 2 + 1, screen.height, desc -> {
					this.descriptionWidget.setOptionDescription(desc);
				})
		);

		this.descriptionWidget = new OptionDescriptionWidget(
				() -> new ScreenRect(
						screen.width / 3 * 2 + padding,
						tabArea.getTop() + padding,
						paddedWidth,
						this.searchField.getY() - 1 - tabArea.getTop() - padding * 2
				),
				null
		);

		updateButtons();
	}

	@Override
	public Text getTitle() {
		return category.name();
	}

	@Override
	public void forEachChild(Consumer<ClickableWidget> consumer) {
		consumer.accept(this.optionList);
		consumer.accept(this.saveFinishedButton);
		consumer.accept(this.cancelResetButton);
		consumer.accept(this.undoButton);
		consumer.accept(this.searchField);
		consumer.accept(this.descriptionWidget);
	}

	@Override
	public void refreshGrid(ScreenRect screenRectangle) {

	}

	@Override
	public void tick() {
		this.descriptionWidget.tick();
	}

	@Nullable
	@Override
	public Tooltip getTooltip() {
		return this.tooltip;
	}

	public void updateButtons() {
		this.undoButton.active = false;
		this.saveFinishedButton.setMessage(GuiUtils.translatableFallback("yacl.gui.done", ScreenTexts.DONE));
		this.cancelResetButton.setMessage(Text.translatable("controls.reset"));

		MutableText saveFinishedButtonTooltip = Text.translatable("yacl.gui.finished.tooltip");
		MutableText cancelResetButtonTooltip = Text.translatable("yacl.gui.reset.tooltip");

		//? >=1.20.3 {
		this.saveFinishedButton.setTooltip(new YACLTooltip(saveFinishedButtonTooltip, this.saveFinishedButton));
		this.cancelResetButton.setTooltip(new YACLTooltip(cancelResetButtonTooltip, this.cancelResetButton));
		//?} else {
		/*this.saveFinishedButton.setTooltip(Tooltip.of(saveFinishedButtonTooltip));
		this.cancelResetButton.setTooltip(Tooltip.of(cancelResetButtonTooltip));
		*///?}
	}
}
