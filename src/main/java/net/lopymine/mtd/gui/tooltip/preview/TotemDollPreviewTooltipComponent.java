package net.lopymine.mtd.gui.tooltip.preview;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.extension.ResourceLocationExtension;

@ExtensionMethod(ResourceLocationExtension.class)
public class TotemDollPreviewTooltipComponent implements ClientTooltipComponent {

	private final TotemDollData data;
	private final ResourceLocation modelId;

	public TotemDollPreviewTooltipComponent(TotemDollData data, ResourceLocation modelId) {
		this.data    = data;
		this.modelId = modelId;
		this.data.setStandardMModel(modelId);
	}

	@Override
	public int getHeight() {
		return MyTotemDollConfig.getInstance().getBetterTagMenuTooltipSize() + 10;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return MyTotemDollConfig.getInstance().getBetterTagMenuTooltipSize();
	}

	@Override
	public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
		int width = this.getWidth(textRenderer);
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		float sizeOriginal = config.getBetterTagMenuTooltipSize();
		float size = (sizeOriginal / 1.25F) * config.getTagMenuTooltipModelScale();
		Component text = Component.nullToEmpty(this.modelId.getFileName());
		int textWidth = textRenderer.width(text);

		int height = this.getHeight();
		context.enableScissor(x, y + 10 + 4 + 2, x + width, y + height - 2);

		TotemDollRenderer.renderPreview(context, x, y + 10, width, height - 10, size, this.data, DollRenderContext.D_TOOLTIP);

		context.disableScissor();

		context.enableScissor(x, y, x + width, y + height);
		if (textWidth > width) {
			DrawUtils.drawText(context, text, x, y, width, 10);
		} else {
			context.drawString(textRenderer, text, x, y + 1, -1, true);
		}
		context.fill(x, y + 10 + 3, x + Math.min((textWidth - 5), width), y + 10 + 4, -1);
		context.disableScissor();
	}
}
