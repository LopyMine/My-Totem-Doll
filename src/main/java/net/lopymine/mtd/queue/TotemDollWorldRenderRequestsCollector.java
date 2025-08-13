package net.lopymine.mtd.queue;

import java.util.*;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class TotemDollWorldRenderRequestsCollector {

	private static final TotemDollWorldRenderRequestsCollector INSTANCE = new TotemDollWorldRenderRequestsCollector();

	public static TotemDollWorldRenderRequestsCollector getInstance() {
		return INSTANCE;
	}

	private final MatrixStack matrices = new MatrixStack();
	private final List<TotemDollWorldRenderRequest> requests = new ArrayList<>();

	private TotemDollWorldRenderRequestsCollector() {

	}

	public void requestRender(MatrixStack matrices, TotemDollData data, AbstractClientPlayerEntity holdingPlayer, DollRenderContext context, VertexConsumerProvider provider, int light, int overlay) {
		this.requests.add(new TotemDollWorldRenderRequest(matrices.peek().copy(), data, holdingPlayer, context, provider, light, overlay));
	}

	public void render() {
		for (TotemDollWorldRenderRequest request : this.requests) {
			this.matrices.push();
			this.matrices.peek().copy(request.copyPeek());
			TotemDollRenderer.renderDoll(this.matrices, request.data(), request.holdingPlayer(), request.context(), request.provider(), request.light(), request.overlay());
			this.matrices.pop();
		}
		this.requests.clear();
	}

}
