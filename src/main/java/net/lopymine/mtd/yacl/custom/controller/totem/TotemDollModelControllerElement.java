package net.lopymine.mtd.yacl.custom.controller.totem;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.*;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.yacl.custom.screen.TotemDollModelSelectionScreen;

public class TotemDollModelControllerElement extends ControllerWidget<TotemDollModelController> {

	private final TotemDollModelController controller;
	private final Text selectText;

	public TotemDollModelControllerElement(TotemDollModelController controller, YACLScreen screen, Dimension<Integer> dim) {
		super(controller, screen, dim);
		this.controller = controller;
		this.selectText = MyTotemDoll.text("text.select_text");
	}

	@Override
	protected int getHoveredControlWidth() {
		return this.getUnhoveredControlWidth();
	}

	@Override
	protected Text getValueText() {
		if (this.hovered && this.isAvailable()) {
			return this.selectText;
		}
		return super.getValueText();
	}

	@Override
	protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		Text valueText = this.getValueText();

		int width = textRenderer.getWidth(valueText);
		if (this.getDimension().x() + this.getXPadding() + width > this.getDimension().xLimit() - this.getXPadding()) {
			ClickableWidget.drawScrollableText(graphics, textRenderer, valueText, this.getDimension().x() + this.getXPadding(), this.getDimension().y(), this.getDimension().xLimit() - this.getXPadding(), this.getDimension().yLimit(), -1);
			return;
		}
		super.drawValueText(graphics, mouseX, mouseY, delta);
	}

	//? if >=1.21.9 {
	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		if (this.isAvailable() && this.isMouseOver(click.x(), click.y()) && this.getDimension().isPointInside((int) click.x(), (int) click.y())) {
			this.playDownSound();
			MinecraftClient.getInstance().setScreen(new TotemDollModelSelectionScreen(this.screen, this.controller.option()));
			return true;
		}
		return false;
	}
	//?} else {
	/*@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.isAvailable() && this.isMouseOver(mouseX, mouseY) && this.getDimension().isPointInside((int) mouseX, (int) mouseY)) {
			this.playDownSound();
			MinecraftClient.getInstance().setScreen(new TotemDollModelSelectionScreen(this.screen, this.controller.option()));
			return true;
		}
		return false;
	}
	*///?}
}
