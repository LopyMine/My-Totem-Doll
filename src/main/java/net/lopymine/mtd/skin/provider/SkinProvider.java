package net.lopymine.mtd.skin.provider;

import net.lopymine.mtd.doll.data.TotemDollData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.*;

public interface SkinProvider {

	@NotNull
	TotemDollData getOrLoadDoll(String value);

	Set<String> getLoadedKeys();

	Collection<TotemDollData> getLoadedDolls();

	CompletableFuture<Void> reloadAll();

	CompletableFuture<Void> reloadOne(String value);

	boolean canProcess(String value);
}
