package net.lopymine.mtd.gui.screen;

import net.lopymine.mtd.utils.texture.PlayerSkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.gui.*;
import net.lopymine.mtd.gui.widget.TotemDollModelPreviewWidget;
import net.lopymine.mtd.gui.widget.preview.WelcomeTotemDollModelPreviewWidget;
import net.lopymine.mtd.utils.DrawUtils;

import org.jetbrains.annotations.NotNull;

//? if >=1.21.9 {

import net.minecraft.client.font.MultilineText.Alignment;

//?}

public class WelcomeScreen extends Screen {

	private final Runnable onClose;
	private Area textArea;
	private Area firstDollArea;
	private Area secondDollArea;
	private TotemDollModelPreviewWidget firstDollPreviewWidget;
	private TotemDollModelPreviewWidget secondDollPreviewWidget;
	private MultilineText text;

	//? if =1.20.1 {
	/*private final RotatingCubeMapRenderer backgroundRenderer;
	*///?}

	public WelcomeScreen(Runnable onClose) {
		super(MyTotemDoll.text("welcome_screen.title"));
		this.onClose = onClose;
		//? if =1.20.1 {
		/*this.backgroundRenderer = new RotatingCubeMapRenderer(TitleScreen.PANORAMA_CUBE_MAP);
		*///?}
	}

	@Override
	protected void init() {
		int offset = 20;
		int screenWidth = this.width;
		int screenHeight = this.height;
		this.text = MultilineText.create(MinecraftClient.getInstance().textRenderer, MyTotemDoll.text("welcome_screen.text"), screenWidth - (offset * 2));

		//? if >=1.21.9 {
		int textHeight = (this.text.getLineCount() * 9) + 10;
		//?} else {
		/*int textHeight = (this.text.count() * 9) + 10;
		*///?}
		int textWidth = this.text.getMaxWidth() + 10;

		this.textArea = new Area().size(textWidth, textHeight).centrolizeX(0, screenWidth).y(offset);

		int size = screenHeight - (offset * 2) - offset - textHeight;
		int previewY = this.textArea.getBottom() + offset;
		int previewX = (screenWidth - (size * 2) - offset) / 2;

		Area previewArea = new Area().size(size, size).pos(previewX, previewY);
		this.firstDollArea = previewArea.copy();
		this.secondDollArea = previewArea.copy().x(previewX + size + offset);

		this.firstDollPreviewWidget = this.addSelectableChild(createWelcomeModelPreviewWidget(this.firstDollArea, TotemDollModel.THREE_D_MODEL_id));
		this.secondDollPreviewWidget = this.addSelectableChild(createWelcomeModelPreviewWidget(this.secondDollArea, TotemDollModel.TWO_D_MODEL_ID));

		if (this.firstDollArea.getX() < this.textArea.getX()) {
			this.textArea.x(this.firstDollArea.getX()).width((size * 2) + offset);
		}
	}

	private @NotNull WelcomeTotemDollModelPreviewWidget createWelcomeModelPreviewWidget(Area area, Identifier modelId) {
		Runnable runnable = () -> {
			MyTotemDollConfig config = MyTotemDollConfig.getInstance();
			config.setStandardTotemDollModelValue(modelId);
			this.close();
		};

		WelcomeTotemDollModelPreviewWidget widget = new WelcomeTotemDollModelPreviewWidget(area.getX(), area.getY(), area.getWidth(), runnable);
		widget.updateModel(modelId);

		PlayerSkinUtils.setupClientTextures(widget.getData());

		return widget;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		//? if =1.20.1 {
		/*this.backgroundRenderer.render(delta, 1.0F);
		context.fill(0, 0, this.width, this.height, -1877995504);
		*///?}

		BackgroundRenderer.drawTransparencyWidgetBackground(context, this.textArea.getX(), this.textArea.getY(), this.textArea.getWidth(), this.textArea.getHeight(), true, false);
		//? if >=1.21.9 {
		this.text.draw(context, Alignment.CENTER, this.textArea.getX() + (this.textArea.getWidth() / 2), this.textArea.getY() + 5, 9, true, -1);
		//?} else {
		/*this.text.drawCenterWithShadow(context, this.textArea.getX() + (this.textArea.getWidth() / 2), this.textArea.getY() + 5, 9, -1);
		*///?}

		boolean firstOver = this.firstDollArea.over(mouseX, mouseY);
		BackgroundRenderer.drawTransparencyWidgetBackground(context, this.firstDollArea.getX(), this.firstDollArea.getY(), this.firstDollArea.getWidth(), this.firstDollArea.getHeight(), true, firstOver);
		this.firstDollPreviewWidget.render(context, mouseX, mouseY, delta);

		DrawUtils.drawCenteredText(context, this.firstDollArea.getX() + 10, this.firstDollArea.getY() + 10, this.firstDollArea.getWidth() - 20, MyTotemDoll.text("welcome_screen.option.3d"));

		boolean secondOver = this.secondDollArea.over(mouseX, mouseY);
		BackgroundRenderer.drawTransparencyWidgetBackground(context, this.secondDollArea.getX(), this.secondDollArea.getY(), this.secondDollArea.getWidth(), this.secondDollArea.getHeight(), true, secondOver);
		this.secondDollPreviewWidget.render(context, mouseX, mouseY, delta);

		DrawUtils.drawCenteredText(context, this.secondDollArea.getX() + 10, this.secondDollArea.getY() + 10, this.secondDollArea.getWidth() - 20, MyTotemDoll.text("welcome_screen.option.2d"));
	}

	@Override
	public void close() {
		this.onClose.run();
	}
}
