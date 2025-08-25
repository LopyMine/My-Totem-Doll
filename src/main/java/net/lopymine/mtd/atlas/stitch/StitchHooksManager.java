package net.lopymine.mtd.atlas.stitch;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.lopymine.mtd.client.MyTotemDollClient;
import org.jetbrains.annotations.Nullable;

public class StitchHooksManager {

	private final Queue<OnAtlasStitched> stitchHooks = new ConcurrentLinkedQueue<>();

	public void addHook(@Nullable OnAtlasStitched onAtlasStitched) {
		if (onAtlasStitched != null) {
			this.stitchHooks.add(onAtlasStitched);
		}
	}

	public void runAllHooks() {
		OnAtlasStitched currentHook;
		while ((currentHook = this.stitchHooks.poll()) != null) {
			try {
				currentHook.onStitch();
			} catch (Exception e) {
				MyTotemDollClient.LOGGER.warn("Unexpected error on stitch hooking:", e);
			}
		}
	}

}
