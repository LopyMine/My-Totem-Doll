package net.lopymine.mtd.queue;

import java.util.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.LockableAtlasTexture;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.renderer.*;
import net.lopymine.mtd.extension.MatrixStackEntryExtension;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.*;

@ExtensionMethod(MatrixStackEntryExtension.class)
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
		MatrixStack.Entry entry = matrices.peek();
		this.requests.add(new TotemDollWorldRenderRequest(/*? if >=1.21 {*/ entry.copy() /*?} else {*/ /*new MatrixStack.Entry(new Matrix4f(entry.getPositionMatrix()), new Matrix3f(entry.getNormalMatrix())) *//*?}*/, data, holdingPlayer, context, provider, light, overlay));
	}

	public void render() {
		LockableAtlasTexture atlasTexture = MyTotemDollAtlasManager.getAtlasTexture();
		atlasTexture.setLocked(true);
		for (TotemDollWorldRenderRequest request : this.requests) {
			this.matrices.push();
			this.matrices.peek().copyFrom(request.copyPeek());
			TotemDollRenderer.renderDoll(this.matrices, request.data(), request.holdingPlayer(), request.context(), request.provider(), request.light(), request.overlay());
			this.matrices.pop();
		}
		this.requests.clear();
		atlasTexture.setLocked(false);
	}

}
