package net.lopymine.mtd.queue;

import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack.Entry;

public record TotemDollWorldRenderRequest(Entry copyPeek, TotemDollData data, AbstractClientPlayerEntity holdingPlayer, DollRenderContext context, VertexConsumerProvider provider, int light, int overlay) {

}
