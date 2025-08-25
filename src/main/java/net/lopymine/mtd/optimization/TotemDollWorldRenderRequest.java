package net.lopymine.mtd.optimization;

import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack.Entry;

public record TotemDollWorldRenderRequest(Entry copyPeek, TotemDollData data, TotemDollRenderProperties renderProperties, AbstractClientPlayerEntity holdingPlayer, DollRenderContext context, VertexConsumerProvider provider, int light, int overlay) {

}
