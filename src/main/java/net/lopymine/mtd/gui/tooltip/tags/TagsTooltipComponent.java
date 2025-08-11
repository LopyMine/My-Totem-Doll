package net.lopymine.mtd.gui.tooltip.tags;

import net.minecraft.client.font.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.*;

import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.DrawUtils;

import java.util.*;
import java.util.Map.Entry;

public class TagsTooltipComponent implements TooltipComponent {

	private final Map<Identifier, Text> rows = new HashMap<>();

	public TagsTooltipComponent(String tags) {
		TagsManager.getRegisteredTags(tags).forEach((i) -> {
			char c = (char) i;
			this.rows.put(TagsManager.getTagIcon(c), TagsManager.getAppliedTagDescription(c));
		});
	}

	@Override
	public int getHeight(/*? >=1.21.2 {*/TextRenderer textRenderer/*?}*/) {
		return 10 * this.rows.size();
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		int maxWidth = 0;
		for (Text text : this.rows.values()) {
			int textWidth = textRenderer.getWidth(text) + 10;
			maxWidth = Math.max(maxWidth, textWidth);
		}
		return maxWidth;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y,/*? >=1.21.2 {*/int w, int h,/*?}*/ DrawContext context) {
		int yOffset = 0;

		int space = textRenderer.getWidth(ScreenTexts.space());
		for (Entry<Identifier, Text> entry : this.rows.entrySet()) {
			DrawUtils.drawTexture(context, entry.getKey(), x + space, y + yOffset - 1, 0, 0, 10, 10, 10, 10);
			context.drawText(textRenderer, entry.getValue(), x + space + 10 + 4, y + yOffset, -1, true);
			yOffset += 10;
		}
	}
}
