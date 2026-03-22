package net.lopymine.mtd.gui.screen;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.gui.*;
import net.lopymine.mtd.gui.widget.TotemDollModelPreviewWidget;
import net.lopymine.mtd.gui.widget.preview.WelcomeTotemDollModelPreviewWidget;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class WelcomeScreen extends Screen {

	private final Runnable onClose;
	private Area textArea;
	private Area firstDollArea;
	private Area secondDollArea;
	private TotemDollModelPreviewWidget firstDollPreviewWidget;
	private TotemDollModelPreviewWidget secondDollPreviewWidget;
	private MultiLineLabel text;


	public WelcomeScreen(Runnable onClose) {
		super(MyTotemDoll.text("welcome_screen.title"));
		this.onClose = onClose;
	}

	@Override
	protected void init() {
		int offset = 20;
		int screenWidth = this.width;
		int screenHeight = this.height;
		this.text = MultiLineLabel.create(Minecraft.getInstance().font, MyTotemDoll.text("welcome_screen.text"), screenWidth - (offset * 2));

		int textHeight = (this.text.getLineCount() * 9) + 10;
		int textWidth = this.text.getWidth() + 10;

		this.textArea = new Area().size(textWidth, textHeight).centrolizeX(0, screenWidth).y(offset);

		int size = screenHeight - (offset * 2) - offset - textHeight;
		int previewY = this.textArea.getBottom() + offset;
		int previewX = (screenWidth - (size * 2) - offset) / 2;

		Area previewArea = new Area().size(size, size).pos(previewX, previewY);
		this.firstDollArea  = previewArea.copy();
		this.secondDollArea = previewArea.copy().x(previewX + size + offset);

		this.firstDollPreviewWidget  = this.addWidget(createWelcomeModelPreviewWidget(this.firstDollArea, TotemDollModel.THREE_D_MODEL_id));
		this.secondDollPreviewWidget = this.addWidget(createWelcomeModelPreviewWidget(this.secondDollArea, TotemDollModel.TWO_D_MODEL_ID));

		if (this.firstDollArea.getX() < this.textArea.getX()) {
			this.textArea.x(this.firstDollArea.getX()).width((size * 2) + offset);
		}
	}

	private @NotNull WelcomeTotemDollModelPreviewWidget createWelcomeModelPreviewWidget(Area area, Identifier modelId) {
		Runnable runnable = () -> {
			MyTotemDollConfig config = MyTotemDollConfig.getInstance();
			config.setSelectedStandardTotemDollModelValue(modelId);
			config.setStandardTotemDollModelValue(modelId);
			config.saveAsync();
			this.onClose();
		};

		WelcomeTotemDollModelPreviewWidget widget = new WelcomeTotemDollModelPreviewWidget(area.getX(), area.getY(), area.getWidth(), runnable);
		widget.updateModel(modelId);

		PlayerSkinUtils.setupClientTextures(widget.getData());

		return widget;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(graphics, mouseX, mouseY, delta);

		BackgroundRenderer.drawTransparencyWidgetBackground(graphics, this.textArea.getX(), this.textArea.getY(), this.textArea.getWidth(), this.textArea.getHeight(), true, false);
		this.text.visitLines(TextAlignment.CENTER, this.textArea.getX() + (this.textArea.getWidth() / 2), this.textArea.getY() + 5, 9, graphics.textRenderer());

		boolean firstOver = this.firstDollArea.over(mouseX, mouseY);
		BackgroundRenderer.drawTransparencyWidgetBackground(graphics, this.firstDollArea.getX(), this.firstDollArea.getY(), this.firstDollArea.getWidth(), this.firstDollArea.getHeight(), true, firstOver);
		this.firstDollPreviewWidget.extractRenderState(graphics, mouseX, mouseY, delta);

		DrawUtils.drawCenteredText(graphics, MyTotemDoll.text("welcome_screen.option.3d"), this.firstDollArea.getX() + 10, this.firstDollArea.getY() + 10, this.firstDollArea.getWidth() - 20);

		boolean secondOver = this.secondDollArea.over(mouseX, mouseY);
		BackgroundRenderer.drawTransparencyWidgetBackground(graphics, this.secondDollArea.getX(), this.secondDollArea.getY(), this.secondDollArea.getWidth(), this.secondDollArea.getHeight(), true, secondOver);
		this.secondDollPreviewWidget.extractRenderState(graphics, mouseX, mouseY, delta);

		DrawUtils.drawCenteredText(graphics, MyTotemDoll.text("welcome_screen.option.2d"), this.secondDollArea.getX() + 10, this.secondDollArea.getY() + 10, this.secondDollArea.getWidth() - 20);
	}

	@Override
	public void onClose() {
		this.onClose.run();
	}
}
