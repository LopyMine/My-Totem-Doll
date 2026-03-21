package net.lopymine.mtd.gui.tooltip.preview;

import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.IdentifierExtension;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@ExtensionMethod(IdentifierExtension.class)
public class TotemDollPreviewTooltipComponent implements ClientTooltipComponent {

	private final TotemDollData data;
	private final Identifier modelId;

	public TotemDollPreviewTooltipComponent(TotemDollData data, Identifier modelId) {
		this.data    = data;
		this.modelId = modelId;
		this.data.setStandardMModel(modelId);
	}

	@Override
	public int getHeight(Font textRenderer) {
		return MyTotemDollConfig.getInstance().getBetterTagMenuTooltipSize() + 10;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return MyTotemDollConfig.getInstance().getBetterTagMenuTooltipSize();
	}

	@Override
	public void extractImage(Font textRenderer, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		int width = this.getWidth(textRenderer);
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		float sizeOriginal = config.getBetterTagMenuTooltipSize();
		float size = (sizeOriginal / 1.25F) * config.getTagMenuTooltipModelScale();
		Component text = Component.nullToEmpty(this.modelId.getFileName());
		int textWidth = textRenderer.width(text);

		int height = this.getHeight(textRenderer);
		graphics.enableScissor(x, y + 10 + 4 + 2, x + width, y + height - 2);

		TotemDollRenderer.renderPreview(graphics, x, y + 10, width, height - 10, size, this.data, DollRenderContext.D_TOOLTIP);

		graphics.disableScissor();

		graphics.enableScissor(x, y, x + width, y + height);
		if (textWidth > width) {
			DrawUtils.drawText(graphics, text, x, y, width, 10);
		} else {
			graphics.text(textRenderer, text, x, y + 1, -1, true);
		}
		graphics.fill(x, y + 10 + 3, x + Math.min((textWidth - 5), width), y + 10 + 4, -1);
		graphics.disableScissor();
	}
}
