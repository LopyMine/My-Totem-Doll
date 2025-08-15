package net.lopymine.mtd.thread;

import net.lopymine.mtd.client.MyTotemDollClient;

import java.util.*;
import java.util.concurrent.*;

// Basically, for now I need it only for downloading totems
public class MyTotemDollTaskExecutor {

	public static ExecutorService MAIN_EXECUTOR = Executors.newFixedThreadPool(MyTotemDollClient.getConfig().getParallelTasksCount());

	public static void reload() {
		int threadsCount = MyTotemDollClient.getConfig().getParallelTasksCount();
		List<Runnable> runnables = MAIN_EXECUTOR.shutdownNow();
		MAIN_EXECUTOR = Executors.newFixedThreadPool(threadsCount);
		for (Runnable runnable : runnables) {
			MAIN_EXECUTOR.submit(runnable);
		}
	}

	public static void stop() {
		MAIN_EXECUTOR.shutdown();
	}

	public static CompletableFuture<Void> execute(Runnable runnable) {
		return CompletableFuture.runAsync(runnable, MAIN_EXECUTOR);
	}
}
