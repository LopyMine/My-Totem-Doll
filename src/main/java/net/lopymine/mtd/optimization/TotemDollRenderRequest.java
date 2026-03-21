package net.lopymine.mtd.optimization;

import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

public record TotemDollRenderRequest(
		Pose copyPeek,
		TotemDollData data,
		TotemDollRenderProperties renderProperties,
		AbstractClientPlayer holdingPlayer,
		DollRenderContext context,
		int light,
		int overlay,
		int outlineColor,
		@Nullable MultiBufferSource provider) {

}
