package net.lopymine.mtd.gui.widget.list;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;

@Getter
public abstract class AbstractVersionedEntryListWidget<E extends Entry<E>> extends ContainerObjectSelectionList<E> {

	public AbstractVersionedEntryListWidget(int x, int y, int width, int height, int itemHeight) {
		super(Minecraft.getInstance(), width, height, y, itemHeight);
		this.setX(x);
	}

	@Override
	protected void renderSelection(GuiGraphics context, E entry, int color) {
	}

	@Override
	protected void renderListSeparators(GuiGraphics context) {
	}

	@Override
	protected void renderListItems(GuiGraphics context, int mouseX, int mouseY, float delta) {
		this.startScissor(context);
		super.renderListItems(context, mouseX, mouseY, delta);
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
}
