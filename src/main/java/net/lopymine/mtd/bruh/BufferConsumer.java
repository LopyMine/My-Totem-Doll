package net.lopymine.mtd.bruh;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface BufferConsumer {

	VertexConsumer accept(RenderType type);

}
