package net.lopymine.mtd.gui.widget.button;

import java.util.List;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

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
	public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
		this.widget.extractRenderState(context, mouseX, mouseY, deltaTicks);
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		this.widget.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		this.widget.setHeight(height);
	}

	@Override
	public void setX(int x) {
		super.setX(x);
		this.widget.setX(x);
	}

	@Override
	public void setY(int y) {
		super.setY(y);
		this.widget.setY(y);
	}


	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		return this.widget.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
		return this.widget.mouseDragged(click, offsetX, offsetY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		return this.widget.mouseReleased(click);
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
