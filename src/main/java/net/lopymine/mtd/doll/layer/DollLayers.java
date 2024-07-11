package net.lopymine.mtd.doll.layer;

import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.doll.model.DollModel;

public class DollLayers {

	public static final EntityModelLayer STEVE_MODEL_LAYER = new EntityModelLayer(MyTotemDoll.id("steve_doll_model_layer"), "steve_doll_layer");
	public static final EntityModelLayer ALEX_MODEL_LAYER = new EntityModelLayer(MyTotemDoll.id("alex_doll_model_layer"), "alex_doll_layer");

	public static void register() {
		EntityModelLayerRegistry.registerModelLayer(STEVE_MODEL_LAYER, () -> TexturedModelData.of(DollModel.getSteveModelData(), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(ALEX_MODEL_LAYER, () -> TexturedModelData.of(DollModel.getAlexModelData(), 64, 64));
	}
}
