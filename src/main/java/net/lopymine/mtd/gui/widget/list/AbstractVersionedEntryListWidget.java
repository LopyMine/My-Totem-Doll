package net.lopymine.mtd.gui.widget.list;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;

@Getter
public abstract class AbstractVersionedEntryListWidget<E extends Entry<E>> extends ContainerObjectSelectionList<E> {

	public boolean visible = true;

	public AbstractVersionedEntryListWidget(int x, int y, int width, int height, int itemHeight) {
		super(Minecraft.getInstance(), width, height, y , y + height , itemHeight);
		this.setX(x);
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
	}

	@Override
	protected void renderSelection(GuiGraphics context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
	}


	@Override
	protected void renderBackground(GuiGraphics context) {
		this.drawMenuListBackground(context);
	}

	protected void drawMenuListBackground(GuiGraphics context) {

	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		if (!this.visible) {
			return;
		}
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	protected void renderList(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.startScissor(context);
		super.renderList(context, mouseX, mouseY, delta);
		this.endScissor(context);
	}

	protected void endScissor(GuiGraphics context) {
		context.disableScissor();
	}

	protected void startScissor(GuiGraphics context) {
		context.enableScissor(0, this.getY() + 2, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 2);
	}

	public void setListScrollAmount(int i) {
		this.setScrollAmount(i);
	}

	public int getX() {
		return this.x0;
	}

	public int getY() {
		return this.y0;
	}

	public void setX(int x) {
		this.x0 = x;
		this.x1 = this.x0 + this.getWidth();
	}

	public void setY(int y) {
		this.y0 = y;
		this.y1 = this.y0 + this.getHeight();
	}

	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBottom() {
		return this.getY() + this.getHeight();
	}

	public int getRight() {
		return this.getX() + this.getWidth();
	}


	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + this.getX();
	}
}
