package net.lopymine.mtd.gui.widget.button;

import lombok.Getter;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

import java.util.List;

@Getter
public class ButtonListEntryWidget extends Entry<ButtonListEntryWidget> {

	private final Button widget;

	public ButtonListEntryWidget(Component text, OnPress pressAction) {
		this.widget = Button.builder(text, pressAction).build();
	}

	@Override
	public List<? extends NarratableEntry> narratables() {
		return List.of(this.widget);
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return List.of(this.widget);
	}

	@Override
	public void render(GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		this.widget.setWidth(entryWidth);
		this.widget.setX(x);

		this.widget.setY(y + ((entryHeight - 20) / 2));
		this.widget.render(context, mouseX, mouseY, tickDelta);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.widget.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.widget.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		this.widget.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.widget.isMouseOver(mouseX, mouseY);
	}
}
