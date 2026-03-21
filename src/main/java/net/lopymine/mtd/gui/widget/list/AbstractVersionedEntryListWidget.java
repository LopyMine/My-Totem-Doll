package net.lopymine.mtd.gui.widget.list;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;

@Getter
public abstract class AbstractVersionedEntryListWidget<E extends Entry<E>> extends ContainerObjectSelectionList<E> {

	public AbstractVersionedEntryListWidget(int x, int y, int width, int height, int itemHeight) {
		super(Minecraft.getInstance(), width, height, y, itemHeight);
		this.setX(x);
	}

	@Override
	protected void extractScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {

	}

	@Override
	protected void extractSelection(GuiGraphicsExtractor context, E entry, int color) {
	}

	@Override
	protected void extractListSeparators(GuiGraphicsExtractor context) {
	}

	@Override
	protected void extractListItems(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		this.startScissor(context);
		super.extractListItems(context, mouseX, mouseY, delta);
		this.endScissor(context);
	}

	protected void endScissor(GuiGraphicsExtractor context) {
		context.disableScissor();
	}

	protected void startScissor(GuiGraphicsExtractor context) {
		context.enableScissor(0, this.getY() + 2, this.getX() + this.getWidth(), this.getY() + this.getHeight() - 2);
	}

	public void setListScrollAmount(int i) {
		this.setScrollAmount(i);
	}
}
