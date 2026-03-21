package net.lopymine.mtd.gui.widget.list;

import java.util.*;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.client.gui.components.events.*;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.*;

@Getter
@Setter
public abstract class AbstractSearchListWidget<E extends Entry<E>> extends AbstractVersionedEntryListWidget<E> {

	public static final MutableComponent NOTHING_FOUND_TEXT = MyTotemDoll.text("text.nothing_found");

	private final List<E> savedWidgets = new ArrayList<>();
	private boolean searching = false;

	public AbstractSearchListWidget(int x, int y, int width, int height, int itemHeight) {
		super(x, y, width, height, itemHeight);
	}

	@Override
	protected void extractListItems(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		this.startScissor(context);
		for (E entry : this.getWidgets()) {
			if (entry.getY() + entry.getHeight() >= this.getY() && entry.getY() <= this.getBottom()) {
				this.extractItem(context, mouseX, mouseY, delta, entry);
			}
		}
		this.endScissor(context);
	}

	public void setFocused(@Nullable GuiEventListener focused) {
		E entry = this.getFocused();
		if (entry != focused && entry instanceof ContainerEventHandler parentElement) {
			parentElement.setFocused(null);
		}

		super.setFocused(focused);
		int i = this.getWidgets().indexOf(focused);
		if (i >= 0) {
			this.setSelected(this.getWidgets().get(i));
		}

	}

	protected List<E> getWidgets() {
		return this.children();
	}

	@Override
	protected void extractListBackground(GuiGraphicsExtractor context) {
		if (this.searching && this.children.isEmpty()) {
			int a = (this.getWidth() - this.getRowWidth()) / 2;
			DrawUtils.drawText(context, NOTHING_FOUND_TEXT, this.getX() + a, this.getY(), this.getWidth() - a, this.getHeight() + 4);
		}
	}

	@Override
	public int getItemCount() {
		return super.getItemCount();
	}

	public void search(String string) {
		this.setListScrollAmount(0);

		if (string.isEmpty()) {
			if (!this.savedWidgets.isEmpty()) {
				this.setSelected(null);
				this.children.clear();
				this.children.addAll(this.savedWidgets);
			}
			this.searching = false;
			this.updateCurrentWidgets();
			return;
		}

		if (!this.searching) {
			this.savedWidgets.clear();
			this.savedWidgets.addAll(this.children);
		}
		this.setSelected(null);
		this.children.clear();

		for (E child : this.savedWidgets) {
			if (this.searched(string, child)) {
				this.children.add(child);
			}
		}

		this.children.sort(this.getComparator());
		this.searching = true;
		this.updateCurrentWidgets();
	}

	private void updateCurrentWidgets() {
		this.setScrollAmount(0);
	}

	protected abstract @NotNull Comparator<E> getComparator();

	protected abstract boolean searched(String string, E child);

}
