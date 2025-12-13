package net.lopymine.mtd.yacl.custom.renderer;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.DrawContextExtension;
import net.lopymine.mtd.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.totem.TotemDollSkinType;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.gui.BackgroundRenderer;
import net.lopymine.mtd.utils.plugin.TotemDollPlugin;

import org.jetbrains.annotations.Nullable;

//? if >=1.21.11 {
import net.minecraft.client.font.Alignment;
//?}

//? if >=1.21.9 && <=1.21.10 {
/*
import net.minecraft.client.font.MultilineText.Alignment;
*/
//?}

@ExtensionMethod(DrawContextExtension.class)
public class TotemDollPreviewRenderer implements ImageRenderer {

	private static final int STANDARD_SUGGESTION_TEXT_COLOR = ColorUtils.getArgb(255, 79, 64);
	private static final int HOLDING_PLAYER_COLOR = ColorUtils.getArgb(212, 120, 28);

	private TotemDollData data;
	@Nullable
	private MultilineText suggestionText;
	@Nullable
	private TotemDollSkinType suggestionSkinType;

	private int lastRenderWidth;

	public TotemDollPreviewRenderer() {
		this.data = StandardTotemDollManager.getStandardDoll();
	}

	@Override
	public int render(DrawContext context, int x, int y, int renderWidth, float tickDelta) {
		int offset = 5;
		int width = renderWidth - (offset * 2);

		this.renderDollStatus(context, x + offset, y + offset, width);
		this.updateSuggestion(width, this.lastRenderWidth != renderWidth);
		this.lastRenderWidth = renderWidth;

		int i = this.renderSuggestionText(context, x + offset, y + offset + 30 + 10, width);
		return (this.renderDoll(context, x + offset, i, width) + offset) - y;
	}

	private void updateSuggestion(int width, boolean resized) {
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		TotemDollSkinType skinType = config.getStandardTotemDollSkinType();
		String skinValue = config.getStandardTotemDollSkinValue();

		TotemDollSkinType type = this.suggestionSkinType;

		if ((skinType.isNeedData() && skinValue.isEmpty()) && skinType != TotemDollSkinType.STEVE || skinType == TotemDollSkinType.HOLDING_PLAYER) {
			this.suggestionSkinType = skinType;
		} else {
			this.suggestionSkinType = null;
			this.suggestionText     = null;
		}

		if (this.suggestionSkinType != null && (type != this.suggestionSkinType || resized)) {
			this.suggestionText = MultilineText.create(textRenderer, this.suggestionSkinType.getSuggestionText(), width - 5);
		}
	}

	private int renderSuggestionText(DrawContext context, int x, int y, int width) {
		int suggestionColor = this.getSuggestionColors();

		if (this.suggestionText == null) {
			return y;
		}

		context.push();
		context.translate(0, 0, 10);
		//? if >=1.21.11 {
		int i = this.suggestionText.draw(Alignment.LEFT, x + 5, y + 5, 10, context.getTextConsumer());
		//?} elif >=1.21.9 {
		/*int i = this.suggestionText.draw(context, Alignment.LEFT, x + 5, y + 5, 10, false, suggestionColor);
		*///?} else {
		/*int i = this.suggestionText.draw(context, x + 5, y + 5, 10, suggestionColor);
		*///?}
		context.translate(0, 0, -5);
		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, width, i - y + 5, true, suggestionColor);

		context.pop();

		return i + 5 + 10;
	}

	private int getSuggestionColors() {
		if (this.suggestionSkinType == TotemDollSkinType.HOLDING_PLAYER) {
			return HOLDING_PLAYER_COLOR;
		}
		return STANDARD_SUGGESTION_TEXT_COLOR;
	}

	private void renderDollStatus(DrawContext context, int x, int y, int width) {
		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, width, 30, true, true);

		DrawUtils.drawCenteredText(context, MyTotemDoll.text("text.status").append(this.data.getStandardSprites().getState().getText()), x + 2, y + 15, x + width - 2);
	}

	private int renderDoll(DrawContext context, int x, int y, int size) {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();

		BackgroundRenderer.drawTransparencyWidgetBackground(context, x, y, size, size, true, true);

		TotemDollRenderer.renderPreview(context, x, y, size, size, size / 1.5F, config.isUseVanillaTotemModel() || TotemDollPlugin.isGoodStick(config.getStandardTotemDollSkinValue()) ? null : this.data.refreshAndApplyRenderProperties());

		return y + size + 2;
	}

	@Override
	public void close() {

	}

	public void updateDoll() {
		this.data = StandardTotemDollManager.initializeStandardDollData();
	}

	public void updateDollState(boolean recreateModel) {
		this.data = StandardTotemDollManager.updateDoll(recreateModel);
	}
}
