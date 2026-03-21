package net.lopymine.mtd.gui.widget.info;

import java.util.List;
import lombok.*;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.tooltip.IRequestableTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

@Setter
@Getter
public class InfoWidget implements Renderable {

	public boolean visible = true;
	private int x;
	private int y;
	private int width;
	private int height;

	private boolean hovered;

	private Identifier texture;

	@Nullable
	private TooltipComponent tooltipData;

	public InfoWidget(int x, int y, int width, int height, @Nullable TooltipComponent tooltipData, Identifier texture) {
		this.x           = x;
		this.y           = y;
		this.width       = width;
		this.height      = height;
		this.texture     = texture;
		this.tooltipData = tooltipData;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		if (!this.isVisible()) {
			return;
		}

		this.hovered = context.containsPointInScissor(mouseX, mouseY) && mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

		DrawUtils.drawTexture(context, this.texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());

		this.requestTooltip();
	}

	public void requestTooltip() {
		Minecraft client = Minecraft.getInstance();
		Screen screen = client.screen;
		if (!(screen instanceof IRequestableTooltipScreen tooltipScreen)) {
			return;
		}

		if (!this.isHovered()) {
			return;
		}

		if (this.tooltipData == null) {
			return;
		}

		ClientTooltipComponent component = ClientTooltipComponent.create(this.tooltipData);
		tooltipScreen.myTotemDoll$requestTooltip(((c, x, y, d) -> {
			DrawUtils.drawTooltip(c, List.of(component), x, y);
		}));
	}

	public void setPosition(int x, int y) {
		this.setX(x);
		this.setY(y);
	}
}
