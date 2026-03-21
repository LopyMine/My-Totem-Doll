package net.lopymine.mtd.cache;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.cache.KnownPlayerUUIDsConfig;

public class KnownPlayerUUIDsConfigManager {

	private static boolean requestedSave = false;

	public static void start() {
		Thread thread = new Thread(() -> {
			while (true) {
				try {
					if (!requestedSave) {
						continue;
					}
					KnownPlayerUUIDsConfig config = KnownPlayerUUIDsConfig.getInstance();
					if (!config.isDirty()) {
						continue;
					}
					config.save();
					config.setDirty(false);
					requestedSave = false;
				} catch (Exception e) {
					MyTotemDollClient.LOGGER.error("Failed to save config:", e);
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	public static void save() {
		requestedSave = true;
	}

}
