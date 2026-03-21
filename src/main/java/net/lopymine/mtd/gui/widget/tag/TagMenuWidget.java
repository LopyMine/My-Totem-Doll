package net.lopymine.mtd.gui.widget.tag;

import java.util.*;
import java.util.stream.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.gui.widget.list.AbstractVersionedEntryListWidget;
import net.lopymine.mtd.gui.widget.tag.TagMenuWidget.TagRow;
import net.lopymine.mtd.tag.*;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.tooltip.IRequestableTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.*;

@ExtensionMethod(ItemStackExtension.class)
public class TagMenuWidget extends AbstractVersionedEntryListWidget<TagRow> {

	public static final Identifier BACKGROUND = MyTotemDoll.id("textures/gui/tag_menu/background_new.png");

	public TagMenuWidget(int x, int y, Renamer renamer) {
		super(x, y, 30, 125, 16);

		List<Tag> list = TagsManager.getRegisteredTags().values().stream().toList();
		for (int i = 0; i < list.size(); i += 2) {
			List<Tag> tags = getRangeOfList(list, i);
			List<TagButtonWidget> widgets = new ArrayList<>();
			for (Tag tag : tags) {
				TagButtonWidget tagButtonWidget = createTagButtonWidget(renamer, tag);
				widgets.add(tagButtonWidget);
			}
			this.addEntry(new TagRow(widgets));
		}

		List<CustomModelTag> customModelIds = TagsManager.getCustomModelIdsTags().values().stream().toList();
		if (!customModelIds.isEmpty()) {
			this.addEntry(new SeparatorRow(MyTotemDoll.text("tag_menu.custom_models.title")));
		}

		List<TagButtonWidget> allCustomModelWidgets = new ArrayList<>();
		for (int i = 0; i < customModelIds.size(); i += 2) {
			List<CustomModelTag> tags = getRangeOfList(customModelIds, i);
			List<TagButtonWidget> tagRowWidget = new ArrayList<>();
			for (CustomModelTag tag : tags) {
				CustomModelTagButtonWidget tagButtonWidget = createCustomModelTagButtonWidget(renamer, tag, allCustomModelWidgets);

				tagRowWidget.add(tagButtonWidget);
				allCustomModelWidgets.add(tagButtonWidget);
			}
			this.addEntry(new TagRow(tagRowWidget));
		}
	}

	private static @NotNull TagButtonWidget createTagButtonWidget(Renamer renamer, Tag tag) {
		char character = tag.getTag();

		TagButtonWidget tagButtonWidget = new TagButtonWidget(tag, 0, 0, (widget) -> {
			updateItemStackName(renamer, widget, character);
		});

		tagButtonWidget.setTooltip(TagsManager.getTagDescription(character));
		return tagButtonWidget;
	}

	private static @NotNull CustomModelTagButtonWidget createCustomModelTagButtonWidget(Renamer renamer, CustomModelTag tag, List<TagButtonWidget> allCustomModelWidgets) {
		char character = tag.getTag();

		return new CustomModelTagButtonWidget(tag, 0, 0, (tagButtonWidget) -> {
			updateItemStackName(renamer, tagButtonWidget, character);

			for (TagButtonWidget widget : allCustomModelWidgets) {
				if (!widget.equals(tagButtonWidget)) {
					widget.setPressed(false);
					updateItemStackName(renamer, widget, widget.getTag().getTag());
				}
			}
		});
	}

	private static @NotNull <E> List<E> getRangeOfList(List<E> list, int startIndex) {
		List<E> tags = new ArrayList<>();
		tags.add(list.get(startIndex));
		if (startIndex + 1 < list.size()) {
			tags.add(list.get(startIndex + 1));
		}
		return tags;
	}

	private static void updateItemStackName(Renamer renamer, TagButtonWidget b, char c) {
		String name = b.isPressed() ? TagsManager.addTag(renamer.getName(), c) : TagsManager.removeTag(renamer.getName(), c);
		renamer.setName(name);
	}

	@Nullable
	private static String getTags(ItemStack stack) {
		Component text = stack.getRealCustomName();
		if (text == null) {
			return null;
		}
		String customName = text.getString();
		return TagsManager.getTagsFromName(customName);
	}

	@Override
	public int getRowWidth() {
		return 30;
	}

	@Override
	protected void extractListBackground(GuiGraphicsExtractor context) {
		//DrawUtils.drawTexture(context, BACKGROUND, this.getX(), this.getY(), 0, 0, 50, 166, 50, 166);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		TagRow entry = this.getEntryAtPosition(mouseX, mouseY);
		if (entry != null && entry.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	public void updateButtons(ItemStack stack) {
		String tags = getTags(stack);

		for (TagButtonWidget widget : this.getAllTagButtons()) {
			if (tags != null) {
				widget.setPressed(tags.contains(widget.getText()));
			} else {
				widget.setPressed(false);
			}
		}
	}

	public void updateCustomModelTagButtons(ItemStack stack) {
		this.updateCustomModelTagButtonsData(stack);
	}

	private void updateCustomModelTagButtonsData(ItemStack stack) {
		TotemDollData totemDollData = stack.getTotemDollData();
		for (CustomModelTagButtonWidget widget : this.getCustomModelTagButtons()) {
			widget.updateData(totemDollData);
		}
	}

	private List<TagButtonWidget> getAllTagButtons() {
		return this.children()
				.stream()
				.map(TagRow::children)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	private List<CustomModelTagButtonWidget> getCustomModelTagButtons() {
		return this.children()
				.stream()
				.map(TagRow::children)
				.flatMap(Collection::stream)
				.flatMap((widget) -> {
					if (widget instanceof CustomModelTagButtonWidget tagButtonWidget) {
						return Stream.of(tagButtonWidget);
					}
					return Stream.empty();
				})
				.collect(Collectors.toList());
	}

	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		this.setScrollAmount(this.scrollAmount());
	}

	public interface Renamer {

		String getName();

		void setName(String name);

	}

	public static class TagRow extends ContainerObjectSelectionList.Entry<TagRow> {

		private final List<TagButtonWidget> buttons;

		public TagRow(List<TagButtonWidget> buttons) {
			this.buttons = buttons;
		}

		@Override
		public List<TagButtonWidget> narratables() {
			return this.buttons;
		}

		@Override
		public List<TagButtonWidget> children() {
			return this.buttons;
		}

		@Override
		public void setX(int x) {
			super.setX(x);

			int pos = x;
			for (TagButtonWidget button : this.buttons) {
				button.setX(pos);
				pos += button.getWidth() + 2;
			}
		}

		@Override
		public void setY(int y) {
			super.setY(y);
			for (TagButtonWidget button : this.buttons) {
				button.setY(y);
			}
		}

		@Override
		public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			for (TagButtonWidget widget : this.buttons) {
				widget.setCanBeHovered(hovered);
				widget.extractRenderState(context, mouseX, mouseY, tickDelta);
			}
		}
	}

	public static class SeparatorRow extends TagRow {

		public static final Identifier SEPARATOR = MyTotemDoll.id("textures/gui/tag_menu/separator.png");

		private final Component text;

		public SeparatorRow(Component text) {
			super(new ArrayList<>());
			this.text = text;
		}

		@Override
		public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.render(context, this.getY(), this.getX(), this.getHeight(), hovered);
		}

		private void render(GuiGraphicsExtractor context, int y, int x, int entryHeight, boolean hovered) {
			Minecraft client = Minecraft.getInstance();
			Font textRenderer = client.font;

			DrawUtils.drawTexture(context, SEPARATOR, x - 1, y + (entryHeight / 2) - 3, 0, 0, 32, 7, 32, 7);

			if (hovered) {
				if (!(client.screen instanceof IRequestableTooltipScreen tooltipScreen)) {
					return;
				}

				tooltipScreen.myTotemDoll$requestTooltip(((c, mx, my, d) -> {
					DrawUtils.drawTooltip(context, textRenderer.split(this.text, 10000).stream().map(ClientTooltipComponent::create).collect(Collectors.toList()), mx, my);
				}));
			}
		}
	}

}
