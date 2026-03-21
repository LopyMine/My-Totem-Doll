package net.lopymine.mtd.gui.widget.preview;

import lombok.*;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.gui.widget.TotemDollModelPreviewWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

@Getter
@Setter
public class WelcomeTotemDollModelPreviewWidget extends TotemDollModelPreviewWidget {

	private final Runnable onClick;
	private long hoverTime = 0L;
	private boolean wasHovered = false;
	private boolean focused;

	public WelcomeTotemDollModelPreviewWidget(int x, int y, float size, Runnable onClick) {
		super(x, y, size);
		this.setData(StandardTotemDollManager.getSteveDoll());
		this.onClick = onClick;
	}

	@Override
	protected void renderPreview(GuiGraphicsExtractor context) {
		long a = this.isHovered() ? 1L : -1L;
		long time = this.getHoverTime() + a;
		if (time < this.getMaxHoverTime() && time > 0L) {
			this.setHoverTime(time);
		}

		float scale = 1.0F;

		if (this.getHoverTime() > 0L) {
			scale += this.easeOutSine(Mth.clamp((float) this.getHoverTime() / this.getMaxHoverTime(), 0.0F, 1.0F)) * 0.25F;
		}

		TotemDollRenderer.renderPreview(context, this.getX(), this.getY(), (int) this.getSize(), (int) this.getSize(), this.getSize() * scale, this.getData().refreshAndApplyRenderProperties());
	}

	private long getMaxHoverTime() {
		return 15L;
	}

	private float easeOutSine(float progress) {
		return -(Mth.cos((float) (Math.PI * progress)) - 1) / 2;
	}

	@Override
	public void onClick(MouseButtonEvent click, boolean doubled) {
		this.onClick.run();
	}
}
