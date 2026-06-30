package net.lopymine.mtd.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

public record TotemDollFeature(
		Pose copyPeek,
		TotemDollData data,
		TotemDollRenderProperties renderProperties,
		int light,
		int overlay,
		int outline
) {

	public TotemDollFeature(PoseStack stack, TotemDollData data) {
		this(stack.last().copy(), data, data.getRenderProperties().copy(), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

}
