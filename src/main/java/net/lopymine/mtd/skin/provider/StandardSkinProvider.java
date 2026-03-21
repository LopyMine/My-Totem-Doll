package net.lopymine.mtd.skin.provider;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import lombok.*;
import net.lopymine.mtd.api.Response;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.totem.TotemDollArmsType;
import net.lopymine.mtd.doll.data.*;
import net.lopymine.mtd.doll.manager.StandardTotemDollManager;
import net.lopymine.mtd.skin.data.ParsedSkinData;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.lopymine.mtd.utils.texture.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

@Setter
@Getter
public abstract class StandardSkinProvider implements SkinProvider {

	private final Map<String, CompletableFuture<Void>> reloadingFutures = new ConcurrentHashMap<>();
	private final Map<String, TotemDollData> cache = new ConcurrentHashMap<>();

	private boolean maxRequestsCheckEnabled;
	private int requestsCount = 0;
	private long lastRequestTime = 0L;

	protected StandardSkinProvider(boolean maxRequestsCheckEnabled) {
		this.maxRequestsCheckEnabled = maxRequestsCheckEnabled;
	}

	@Override
	public @NotNull TotemDollData getOrLoadDoll(String value) {
		if (!this.canProcess(value)) {
			return StandardTotemDollManager.getStandardDoll();
		}

		TotemDollData totemDollData = this.getDataOrCreate(value);

		if (totemDollData.getStandardSprites().canStartDownloading()) {
			this.loadDoll(value, this.maxRequestsCheckEnabled, totemDollData);
		}

		return totemDollData;
	}


	public CompletableFuture<Void> loadDoll(String value, boolean checkMaxRequests, TotemDollData totemDollData) {
		if (checkMaxRequests) {
			// Max 10 requests per second
			long now = System.currentTimeMillis();
			if (now - this.lastRequestTime > 1000) {
				this.requestsCount   = 0;
				this.lastRequestTime = now;
			}
			if (this.requestsCount >= 10) {
				return CompletableFuture.completedFuture(null);
			}
			this.requestsCount++;
		}

		totemDollData.getStandardSprites().setState(LoadingState.WAITING_DOWNLOADING);

		return MyTotemDollTaskExecutor.execute(() -> {
			int waitTime = 0;

			while (true) {
				TotemDollSprites textures = totemDollData.getStandardSprites();
				textures.setState(LoadingState.DOWNLOADING);

				Response<ParsedSkinData> response = this.loadDollFromAPI(value);
				if (response.value() == null) {
					LoadingState state = switch (response.statusCode()) {
						case 404 -> LoadingState.NOT_FOUND; // Not Found
						case 429 -> LoadingState.ERROR; // Too many requests
						default -> LoadingState.CRITICAL_ERROR;
					};

					if (state == LoadingState.ERROR) { // Too many requests, we can retry
						try {
							waitTime += 1000;
							Thread.sleep(waitTime);
							continue;
						} catch (Exception e) {
							textures.setState(LoadingState.CRITICAL_ERROR);
							return;
						}
					}

					textures.setState(state);
					return;
				}

				textures.setState(LoadingState.REGISTERING);

				ParsedSkinData parsedSkinData = response.value();
				if (parsedSkinData.getSkinUrl() == null) {
					textures.setState(LoadingState.CRITICAL_ERROR);
					return;
				}

				TotemDollArmsType armsType = TotemDollArmsType.of(parsedSkinData.isSlim());
				textures.setStandardArmsType(armsType);

				Identifier skinId = this.getSkinId(value);

				FailedAction onFailed = (throwable) -> {
					textures.setState(LoadingState.CRITICAL_ERROR);
					MyTotemDollClient.LOGGER.warn("Failed to download doll skin:", throwable);
				};

				SuccessAction onSuccess = (sprite) -> {
					textures.setSkinSprite(sprite);
					textures.setState(LoadingState.DOWNLOADED);
				};

				PlayerSkinUtils.downloadSkin(parsedSkinData.getSkinUrl(), skinId, onSuccess, onFailed, true);

				if (parsedSkinData.getCapeUrl() != null) {
					Identifier capeId = this.getCapeId(value);
					PlayerSkinUtils.downloadSkin(parsedSkinData.getCapeUrl(), capeId, textures::setCapeSprite, null, false);
				}

				if (parsedSkinData.getElytraUrl() != null) {
					Identifier elytraId = this.getElytraId(value);
					PlayerSkinUtils.downloadSkin(parsedSkinData.getElytraUrl(), elytraId, textures::setElytraSprite, null, false);
				}

				break;
			}
		});
	}

	private TotemDollData getDataOrCreate(String value) {
		return Optional.ofNullable(this.getFromCache(value))
				.orElseGet(() -> {
					TotemDollData data = this.createNewDoll(value);
					this.putToCache(value, data);
					return data;
				});
	}

	@Override
	public Set<String> getLoadedKeys() {
		return this.cache.keySet();
	}

	@Override
	public Collection<TotemDollData> getLoadedDolls() {
		return this.cache.values();
	}

	@Override
	public CompletableFuture<Void> reloadAll() {
		Set<CompletableFuture<?>> list = new HashSet<>();

		for (Entry<String, TotemDollData> entry : this.cache.entrySet()) {
			String key = entry.getKey();
			TotemDollData value = entry.getValue();

			CompletableFuture<Void> future = this.reloadingFutures.get(key);
			if (future != null) {
				list.add(future);
				continue;
			}

			list.add(this.reloadDataAndRegisterFuture(key, value));
		}

		return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
	}

	@Override
	public CompletableFuture<Void> reloadOne(String value) {
		CompletableFuture<Void> future = this.reloadingFutures.get(value);
		if (future != null) {
			return future;
		}

		TotemDollData totemDollData = this.getFromCache(value);
		if (totemDollData == null) {
			return CompletableFuture.completedFuture(null);
		}

		return this.reloadDataAndRegisterFuture(value, totemDollData);
	}

	private CompletableFuture<Void> reloadDataAndRegisterFuture(String value, TotemDollData totemDollData) {
		TotemDollSprites textures = totemDollData.getStandardSprites();
		textures.destroy();

		CompletableFuture<Void> future = this.loadDoll(value, false, totemDollData)
				.whenComplete((r, e) -> {
					this.reloadingFutures.remove(value);
					if (e != null) {
						MyTotemDollClient.LOGGER.error("Failed to reload doll data for \"{}\": ", value, e);
					}
				});
		this.reloadingFutures.put(value, future);
		return future;
	}

	protected abstract Response<ParsedSkinData> loadDollFromAPI(String value);

	public abstract TotemDollData createNewDoll(String value);

	@Nullable
	protected TotemDollData getFromCache(String value) {
		return this.cache.get(value);
	}

	protected void putToCache(String value, TotemDollData data) {
		this.cache.put(value, data);
	}

	protected Identifier getSkinId(String value) {
		return this.getId(value, "skin");
	}

	protected Identifier getCapeId(String value) {
		return this.getId(value, "cape");
	}

	protected Identifier getElytraId(String value) {
		return this.getId(value, "elytra");
	}

	protected abstract Identifier getId(String value, String type);
}
