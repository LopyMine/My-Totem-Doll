package net.lopymine.mtd.bruh;

import net.lopymine.mtd.doll.data.TotemDollData;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

public record TotemDollRenderState (
		TotemDollData data,
		int light,
		int overlay,
		int outline
) {

	public TotemDollRenderState(TotemDollData data) {
		this(data, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

}
