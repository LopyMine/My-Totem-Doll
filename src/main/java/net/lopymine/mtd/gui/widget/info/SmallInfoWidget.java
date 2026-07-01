package net.lopymine.mtd.gui.widget.info;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.gui.tooltip.info.InfoTooltipData;
import net.lopymine.mtd.utils.ColorUtils;
import net.minecraft.resources.ResourceLocation;

public class SmallInfoWidget extends InfoWidget {

	public static final ResourceLocation TEXTURE = MyTotemDoll.id("textures/gui/info/info_small.png");
	public static final int TITLE_COLOR = ColorUtils.getArgb(89, 206, 255);

	public SmallInfoWidget(int x, int y) {
		super(x, y, 9, 10, new InfoTooltipData("tags.info", TITLE_COLOR), TEXTURE);
	}
}
