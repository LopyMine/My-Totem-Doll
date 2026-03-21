package net.lopymine.mtd.thread;

import java.util.List;
import java.util.concurrent.*;
import net.lopymine.mtd.config.MyTotemDollConfig;

public class MyTotemDollTaskExecutor {

	public static ExecutorService MAIN_EXECUTOR = Executors.newFixedThreadPool(MyTotemDollConfig.getInstance().getParallelTasksCount());

	public static void reload() {
		int threadsCount = MyTotemDollConfig.getInstance().getParallelTasksCount();
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
