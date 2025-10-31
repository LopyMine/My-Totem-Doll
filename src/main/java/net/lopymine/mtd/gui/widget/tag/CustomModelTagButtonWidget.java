package net.lopymine.mtd.gui.widget.tag;

import lombok.*;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.model.base.MModel;
import net.lopymine.mtd.utils.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.gui.tooltip.preview.TotemDollPreviewTooltipData;
import net.lopymine.mtd.tag.*;
import net.lopymine.mtd.tag.manager.TagsManager;

import java.util.Optional;
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
		this.data = StandardTotemDollManager.getStandardDoll().copy();
	}

	public void updateData(TotemDollData data) {
		if (data == null) {
			return;
		}
		this.data.getRenderProperties().copyFrom(data.getRenderProperties());
	}

	@Override
	public void /*? if >=1.21 {*/renderWidget/*?} else {*//*renderButton*//*?}*/(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.model != null) {
			this.data.setFrameMModel(this.model);
		}
		super./*? if >=1.21 {*/renderWidget/*?} else {*//*renderButton*//*?}*/(context, mouseX, mouseY, delta);
		if (!this.tooltipDataActive) {
			this.tooltipData = null;
		}
		this.tooltipDataActive = false;
	}

	@Override
	protected void renderIcon(DrawContext context, int x, int y) {
		context.enableScissor(this.getX() + 1, this.getY() + 1, this.getX() + this.getWidth() - 1, this.getY() + this.getHeight() - 1);
		TotemDollRenderer.renderPreview(context, x, y, this.getWidth(), this.getHeight(),  Math.min(this.getWidth(), this.getHeight()), this.getData());
		context.disableScissor();
	}

	@Override
	public @Nullable TooltipComponent getTooltipComponent() {
		if (this.model == null) {
			return TooltipComponent.of(Text.of("Unknown Model").asOrderedText());
		}
		if (this.tooltipData == null) {
			this.tooltipData = this.data.copy();
			this.tooltipData.setStandardMModel(this.data.getRenderProperties().getStandardMModel());
		}
		this.tooltipDataActive = true;
		return TooltipComponent.of(new TotemDollPreviewTooltipData(this.tooltipData, this.model));
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, /*? if >=1.21 {*/ double horizontalAmount, /*?}*/ double verticalAmount) {
		if (!this.isMouseOver(mouseX, mouseY)) {
			return false;
		}
		int amount = ((int) verticalAmount) > 0 ? 1 : -1;
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (ScreenUtils.hasShiftDown()) {
			config.setBetterTagMenuTooltipSize(MathHelper.clamp(config.getBetterTagMenuTooltipSize() + (amount * 2), 60, 500));
			return true;
		} else if (ScreenUtils.hasControlDown()) {
			config.setTagMenuTooltipModelScale(MathHelper.clamp(config.getTagMenuTooltipModelScale() + (amount / 12F), 0.1F, 10F));
			return true;
		}
		return false;
	}
}
