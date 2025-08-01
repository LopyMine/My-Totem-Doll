package net.lopymine.mtd.pack;

import java.util.concurrent.*;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.doll.data.TotemDollTextures;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import net.fabricmc.fabric.api.resource.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.minecraft.util.profiler.*;

public class MyTotemDollReloadListener implements IdentifiableResourceReloadListener {

	public static void register() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new MyTotemDollReloadListener());
	}

	@Override
	public Identifier getFabricId() {
		return MyTotemDoll.id("reload_listener");
	}


	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
			Profiler profiler = Profilers.get();
			profiler.push("listener");
			this.reloadStuff(synchronizer, manager, applyExecutor);
			profiler.pop();
		}, applyExecutor);
	}

	private void reloadStuff(Synchronizer synchronizer, ResourceManager resourceManager, Executor applyExecutor) {
		BlockBenchModelManager.reload();
		TotemDollModelFinder.reload(resourceManager);
		TagsManager.reloadCustomModelIdsTags();
		this.reloadAtlas(applyExecutor, synchronizer);
	}

	private void reloadAtlas(Executor applyExecutor, Synchronizer synchronizer) {
		MyTotemDollAtlasManager.clear();
		MyTotemDollAtlasManager.MODEL_TEXTURES.forEach(MyTotemDollAtlasManager::registerSprite);
		MyTotemDollAtlasManager.SPRITES.add(MissingSprite.createSpriteContents());
		MyTotemDollAtlasManager.registerSprite(TotemDollTextures.STEVE_SKIN);
		TotemDollAtlasReloadListener.reloadAtlas(MyTotemDollAtlasManager.SPRITES, applyExecutor, synchronizer);
	}
}
