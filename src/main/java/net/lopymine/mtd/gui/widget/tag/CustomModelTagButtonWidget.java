package net.lopymine.mtd.gui.widget.tag;

import java.util.Optional;
import lombok.*;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.gui.tooltip.preview.TotemDollPreviewTooltipData;
import net.lopymine.mtd.tag.*;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class CustomModelTagButtonWidget extends TagButtonWidget {

	@Nullable
	private final Identifier model;
	private TotemDollData data;
	@Nullable
	private TotemDollData tooltipData;
	private boolean tooltipDataActive = false;

	public CustomModelTagButtonWidget(Tag tag, int x, int y, TagPressAction pressAction) {
		super(tag, x, y, pressAction);
		this.model = Optional.ofNullable(TagsManager.getCustomModelIdsTags().get(tag.getTag())).map(CustomModelTag::getModelId).orElse(null);
		this.data  = StandardTotemDollManager.getStandardDoll().copy();
	}

	public void updateData(TotemDollData data) {
		if (data == null) {
			return;
		}
		this.data.getRenderProperties().copyFrom(data.getRenderProperties());
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (this.model != null) {
			this.data.setFrameMModel(this.model);
		}
		super.extractContents(graphics, mouseX, mouseY, delta);
		if (!this.tooltipDataActive) {
			this.tooltipData = null;
		}
		this.tooltipDataActive = false;
	}

	@Override
	protected void renderIcon(GuiGraphicsExtractor context, int x, int y) {
		context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1, this.getY() + this.getHeight() - 1);
		TotemDollRenderer.renderPreview(context, x, y, this.getWidth(), this.getHeight(), Math.min(this.getWidth(), this.getHeight()), this.getData());
		context.disableScissor();
	}

	@Override
	public @Nullable ClientTooltipComponent getTooltipComponent() {
		if (this.model == null) {
			return ClientTooltipComponent.create(net.minecraft.network.chat.Component.nullToEmpty("Unknown Model").getVisualOrderText());
		}
		if (this.tooltipData == null) {
			this.tooltipData = this.data.copy();
			this.tooltipData.setStandardMModel(this.data.getRenderProperties().getStandardMModel());
		}
		this.tooltipDataActive = true;
		return ClientTooltipComponent.create(new TotemDollPreviewTooltipData(this.tooltipData, this.model));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		}
		int amount = ((int) verticalAmount) > 0 ? 1 : -1;
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (ScreenUtils.hasShiftDown()) {
			config.setBetterTagMenuTooltipSize(Mth.clamp(config.getBetterTagMenuTooltipSize() + (amount * 2), 60, 500));
			return true;
		} else if (ScreenUtils.hasControlDown()) {
			config.setTagMenuTooltipModelScale(Mth.clamp(config.getTagMenuTooltipModelScale() + (amount / 12F), 0.1F, 10F));
			return true;
		}
		return false;
	}
}
