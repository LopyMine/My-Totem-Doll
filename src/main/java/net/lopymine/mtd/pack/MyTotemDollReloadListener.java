package net.lopymine.mtd.pack;

import java.util.concurrent.*;
import net.lopymine.mtd.atlas.manager.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.*;
//? if fabric {
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
//?}
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.minecraft.util.profiling.*;
import net.minecraft.util.profiling.ProfilerFiller;


public class MyTotemDollReloadListener implements /*? if fabric {*/ IdentifiableResourceReloadListener /*?} else {*/ /*PreparableReloadListener *//*?}*/ {

	public static void register() {
		MyTotemDollLoader.registerReloadListener(getId(), new MyTotemDollReloadListener());
	}

	public static ResourceLocation getId() {
		return MyTotemDoll.id("%s-reload-listener".formatted(MyTotemDoll.MOD_ID));
	}

	//? if fabric {
	@Override
	public ResourceLocation getFabricId() {
		return getId();
	}
	//?}

	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
			applyProfiler.startTick();
			applyProfiler.push("listener");
			this.reloadStuff(synchronizer, manager, prepareExecutor, applyExecutor);
			applyProfiler.pop();
			applyProfiler.endTick();
		}, applyExecutor);
	}


	private void reloadStuff(PreparationBarrier synchronizer, ResourceManager resourceManager, Executor prepareExecutor, Executor applyExecutor) {
		this.reloadAtlas(synchronizer, prepareExecutor, applyExecutor);
		BlockBenchModelManager.reload();
		TotemDollModelFinder.reload(resourceManager);
		TagsManager.reloadCustomModelIdsTags();
	}

	private void reloadAtlas(PreparationBarrier synchronizer, Executor prepareExecutor, Executor applyExecutor) {
		MyTotemDollAtlasSpriteManager.reload();
		MyTotemDollAtlasManager.stitchAndUpdate(MyTotemDollAtlasSpriteManager.getSprites(), synchronizer, prepareExecutor, applyExecutor, null);
	}
}
