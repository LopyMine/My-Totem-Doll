package net.lopymine.mtd.gui.widget.preview;

import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.gui.widget.TotemDollModelPreviewWidget;
import net.lopymine.mtd.model.base.MModel;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;

@Getter
@Setter
public class WelcomeTotemDollModelPreviewWidget extends TotemDollModelPreviewWidget {

	private final Runnable onClick;
	private long hoverTime = 0L;
	private boolean wasHovered = false;
	private boolean focused;

	public WelcomeTotemDollModelPreviewWidget(int x, int y, float size, Runnable onClick) {
		super(x, y, size);
		this.onClick = onClick;
	}

	@Override
	protected void renderPreview(DrawContext context) {
		long a = this.isHovered() ? 1L : -1L;
		long time = this.getHoverTime() + a;
		if (time < this.getMaxHoverTime() && time > 0L) {
			this.setHoverTime(time);
		}

		float scale = 1.0F;

		if (this.getHoverTime() > 0L) {
			scale += this.easeOutSine(MathHelper.clamp((float) this.getHoverTime() / this.getMaxHoverTime(), 0.0F, 1.0F)) * 0.25F;
		}

		TotemDollRenderer.renderPreview(context, this.getX(), this.getY(), (int) this.getSize(), (int) this.getSize(), this.getSize() * scale, this.getData().refreshAndApplyRenderProperties());
	}

	private long getMaxHoverTime() {
		return 15L;
	}

	private float easeOutSine(float progress) {
		return -(MathHelper.cos((float) (Math.PI * progress)) - 1) / 2;
	}

	//? if >=1.21.9 {
	@Override
	public void onClick(Click click, boolean doubled) {
		this.onClick.run();
	}
	//?} else {
	/*@Override
	public void onClick(double mouseX, double mouseY) {
		this.onClick.run();
	}
	*///?}
}
