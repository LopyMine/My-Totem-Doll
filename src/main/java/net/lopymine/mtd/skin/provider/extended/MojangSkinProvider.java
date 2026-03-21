package net.lopymine.mtd.skin.provider.extended;

import java.util.*;
import java.util.stream.Collectors;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.api.*;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.skin.data.ParsedSkinData;
import net.lopymine.mtd.skin.provider.StandardSkinProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class MojangSkinProvider extends StandardSkinProvider {

	private static final MojangSkinProvider INSTANCE = new MojangSkinProvider();

	private MojangSkinProvider() {
		super(true);
	}

	public static MojangSkinProvider getInstance() {
		return MojangSkinProvider.INSTANCE;
	}

	@Override
	protected Response<ParsedSkinData> loadDollFromAPI(String value) {
		return MojangAPI.getSkinData(value.toLowerCase());
	}

	@Override
	public TotemDollData createNewDoll(String value) {
		return TotemDollData.create(value);
	}

	@Override
	protected @Nullable TotemDollData getFromCache(String value) {
		return super.getFromCache(value.toLowerCase());
	}

	@Override
	protected void putToCache(String value, TotemDollData data) {
		super.putToCache(value.toLowerCase(), data);
	}

	@Override
	public Set<String> getLoadedKeys() {
		return this.getCache().values().stream().map(TotemDollData::getNickname).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	@Override
	protected Identifier getId(String value, String type) {
		return MyTotemDoll.getDollTextureId("mojang_api/%s/%s".formatted(type, value.toLowerCase()));
	}

	@Override
	public boolean canProcess(String value) {
		if (value == null) {
			return false;
		}

		int length = value.length();
		if (length < 2 || length > 16) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if ((c == '_') || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
				continue;
			}
			return false;
		}

		return true;
	}
}
