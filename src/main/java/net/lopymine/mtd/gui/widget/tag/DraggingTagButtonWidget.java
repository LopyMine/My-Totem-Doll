package net.lopymine.mtd.gui.widget.tag;

import lombok.*;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.other.vector.Vec2i;
import net.lopymine.mtd.tag.Tag;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;

@Getter
@Setter
public class DraggingTagButtonWidget extends TagButtonWidget {

	private final int originalX;
	private final int originalY;
	private int originX;
	private int originY;
	private boolean dragging;

	public DraggingTagButtonWidget(Tag tag, int originX, int originY, int originalX, int originalY, int x, int y, TagPressAction pressAction) {
		super(tag, x, y, pressAction);
		this.originX   = originX;
		this.originY   = originY;
		this.originalX = originalX;
		this.originalY = originalY;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent button, boolean doubled) {
		if (!this.over(button.x(), button.y())) {
			return false;
		}
		if (this.isResetPosButton(button.button())) {
			this.resetPosition();
			return true;
		}
		if (this.isDraggingButton(button.button())) {
			this.setDragging(true);
			return true;
		}
		return super.mouseClicked(button, doubled);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent button, double deltaX, double deltaY) {
		if (this.isDragging() && this.isDraggingButton(button.button())) {
			return true;
		}
		return super.mouseDragged(button, deltaX, deltaY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent button) {
		if (this.isDragging()) {
			this.setDragging(false);
			this.setDraggingPosition((int) button.x(), (int) button.y());
			return true;
		}
		return false;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		this.renderPlease(context, mouseX, mouseY);
	}

	private void renderPlease(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		int x = this.isDragging() ? mouseX - (this.getWidth() / 2) : this.getX();
		int y = this.isDragging() ? mouseY - (this.getHeight() / 2) : this.getY();
		super.renderButton(context, x, y);
		if (!this.isDragging()) {
			this.requestTooltip();
		}
	}

	private void resetPosition() {
		this.setDraggingPosition(this.originalX, this.originalY);
	}

	private void setDraggingPosition(int draggingX, int draggingY) {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		Vec2i pos = config.getTagButtonPos();

		pos.setX((draggingX - (this.getWidth() / 2)) - this.originX);
		pos.setY((draggingY - (this.getHeight() / 2)) - this.originY);

		config.save();

		this.setPosition(draggingX, draggingY);
	}

	private boolean isDraggingButton(int button) {
		return button == 1;
	}

	private boolean isResetPosButton(int button) {
		return button == 2;
	}
}
