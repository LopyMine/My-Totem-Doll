package net.lopymine.mtd.gui.tooltip.tags;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import java.util.*;
import java.util.Map.Entry;
import net.lopymine.mtd.tag.*;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class TagsTooltipComponent implements ClientTooltipComponent {

	private final Map<Identifier, Component> rows = new HashMap<>();
	@Nullable
	private CustomModelTag modelTag;
	@Nullable
	private Component modelTagName;

	public TagsTooltipComponent(String tags) {
		Char2ObjectMap<Tag> registeredTags = TagsManager.getRegisteredTags();
		Map<Character, CustomModelTag> customModelIdsTags = TagsManager.getCustomModelIdsTags();
		TagsManager.getTags(tags).forEach((i) -> {
			char c = (char) i;
			if (registeredTags.containsKey(c)) {
				this.rows.put(TagsManager.getTagIcon(c), TagsManager.getAppliedTagDescription(c));
			} else {
				CustomModelTag modelTag = customModelIdsTags.get(c);
				if (modelTag != null) {
					this.modelTag = modelTag;
				}
			}
		});
		if (this.modelTag != null) {
			this.modelTagName = Component.literal(" > " + this.modelTag.getModelName() + " <").withStyle(ChatFormatting.BLUE);
		}
	}

	@Override
	public int getHeight(Font textRenderer) {
		return 10 * this.rows.size() + (this.modelTagName != null ? 10 : 0);
	}

	@Override
	public int getWidth(Font textRenderer) {
		int maxWidth = 0;
		for (Component text : this.rows.values()) {
			int textWidth = textRenderer.width(text) + 10;
			maxWidth = Math.max(maxWidth, textWidth);
		}
		return maxWidth;
	}

	@Override
	public void extractImage(Font textRenderer, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		int yOffset = 0;

		int space = textRenderer.width(CommonComponents.space());
		for (Entry<Identifier, Component> entry : this.rows.entrySet()) {
			DrawUtils.drawTexture(graphics, entry.getKey(), x + space, y + yOffset - 1, 0, 0, 10, 10, 10, 10);
			graphics.text(textRenderer, entry.getValue(), x + space + 10 + 4, y + yOffset, -1, true);
			yOffset += 10;
		}
		if (this.modelTagName != null) {
			graphics.text(textRenderer, this.modelTagName, x + space, y + yOffset, -1, true);
		}
	}
}
