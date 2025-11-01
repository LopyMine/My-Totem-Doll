package net.lopymine.mtd.optimization;

import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.jetbrains.annotations.Nullable;

public record TotemDollRenderRequest(
		Entry copyPeek,
		TotemDollData data,
		TotemDollRenderProperties renderProperties,
		AbstractClientPlayerEntity holdingPlayer,
		DollRenderContext context,
		int light,
		int overlay,
		int outlineColor,
		@Nullable VertexConsumerProvider provider) {

}
