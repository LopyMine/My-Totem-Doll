package net.lopymine.mtd.pack;

import java.util.concurrent.*;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.manager.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.*;

public class MyTotemDollReloadListener implements PreparableReloadListener {

	public static void register() {
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(getFabricId(), new MyTotemDollReloadListener());
	}

	public static Identifier getFabricId() {
		return MyTotemDoll.id("%s-reload-listener".formatted(MyTotemDoll.MOD_ID));
	}

	@Override
	public CompletableFuture<Void> reload(SharedState store, Executor prepareExecutor, PreparationBarrier synchronizer, Executor applyExecutor) {
		return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
			ProfilerFiller profiler = Profiler.get();
			profiler.push("listener");
			this.reloadStuff(synchronizer, store.resourceManager(), prepareExecutor, applyExecutor);
			profiler.pop();
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
