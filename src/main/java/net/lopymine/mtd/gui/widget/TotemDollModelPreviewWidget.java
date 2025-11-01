package net.lopymine.mtd.gui.widget;

import lombok.*;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.*;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.model.base.MModel;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;

@Getter
@Setter
public class TotemDollModelPreviewWidget extends ClickableWidget {

	private final float size;

	private TotemDollData data;

	private boolean loading;
	private int failedLoadingStatusCode = 0;

	public TotemDollModelPreviewWidget(int x, int y, float size) {
		super(x, y, (int) size, (int) size, Text.of(""));
		this.size = size;
		this.data = StandardTotemDollManager.getStandardDoll().copy();
	}

	@Override
	protected void /*? if >=1.21 {*/renderWidget/*?} else {*//*renderButton*//*?}*/(DrawContext context, int mouseX, int mouseY, float delta) {
		context.enableScissor(this.getX(), this.getY(), (this.getX() + this.getWidth()), (int) (this.getY() + this.getHeight()));
		if (this.loading) {
			this.renderLoadingText(context);
		} else {
			this.renderPreview(context);
		}
		context.disableScissor();
	}

	protected void renderLoadingText(DrawContext context) {
		int halfOfSize = (int) this.size / 2;
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		//context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + 1, -1);
		DrawUtils.drawCenteredText(context, this.getX(), this.getY() + halfOfSize - (textRenderer.fontHeight / 2), (int) this.size, this.getLoadingText(Util.getMeasuringTimeMs()));
	}

	protected void renderPreview(DrawContext context) {
		TotemDollRenderer.renderPreview(context, this.getX(), this.getY(), (int) this.getSize(), (int) this.getSize(), this.getSize() / 1.5F, this.getData().refreshAndApplyRenderProperties());
	}

	public void updateModel(Identifier id) {
		this.loading = true;
		this.failedLoadingStatusCode = 0;
		BlockBenchModelManager.getModelAsyncAsResponse(id, (response) -> {
			MModel value = response.value();
			if (value != null) {
				this.updateModel(value);
				this.loading = false;
			} else {
				this.failedLoadingStatusCode = response.statusCode();
			}
		});
	}

	public void updateModel(MModel model) {
		this.data.setStandardMModel(model);
	}

	private Text getLoadingText(long tick) {
		if (this.failedLoadingStatusCode == 100) {
			return MyTotemDoll.text("text.loading.failed.to_load");
		} else if (this.failedLoadingStatusCode == 102){
			return MyTotemDoll.text("text.loading.failed.unsupported_format");
		}  else if (this.failedLoadingStatusCode > 101 && this.failedLoadingStatusCode < 104){
			return MyTotemDoll.text("text.loading.failed.wrong_metadata");
		}

		int i = (int) (tick / 300L % 4L);
		return MyTotemDoll.text("text.loading.%s".formatted(i));
	}

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {

	}
}
