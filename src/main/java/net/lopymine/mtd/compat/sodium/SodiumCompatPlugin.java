package net.lopymine.mtd.compat.sodium;

import net.fabricmc.loader.api.*;
import net.lopymine.mtd.compat.CompatPlugin;
import org.spongepowered.asm.service.MixinService;

public class SodiumCompatPlugin extends CompatPlugin {

	@Override
	protected String getCompatModId() {
		return "sodium";
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (!super.shouldApplyMixin(targetClassName, mixinClassName)) {
			return false;
		}

		boolean oldMixin = mixinClassName.equals("net.lopymine.mtd.mixin.sodium.ModelPartMixinMixin");
		boolean hotMixin = mixinClassName.equals("net.lopymine.mtd.mixin.sodium.CubeMixinMixin");

		if (hotMixin) {
			return !this.isCurrentVersionOlderThanHot(mixinClassName);
		}

		if (oldMixin) {
			return this.isCurrentVersionOlderThanHot(mixinClassName);
		}

		return true;
	}

	private boolean isCurrentVersionOlderThanHot(String mixinName) {
		FabricLoader fabricLoader = FabricLoader.getInstance();
		ModContainer modContainer = fabricLoader.getModContainer(this.getCompatModId()).orElseThrow();

		Version currentVersion = modContainer.getMetadata().getVersion();
		Version hotVersion = this.getHotSodiumVersion();

		// <6.0.0 (currentOlder == true)
		// ModelPartMixinMixin

		// >=6.0.0 (currentOlder == false)
		// CubeMixin

		boolean bl = currentVersion.compareTo(hotVersion) < 0;
		MixinService.getService().getLogger("[MyTotemDoll: SodiumCompatPlugin]").info("[{}] Detected Sodium, current version older than hot: {}", mixinName, bl);
		return bl;
	}

	private Version getHotSodiumVersion() {
		try {
			return Version.parse("0.6.0+mc1.21.1");
		} catch (VersionParsingException e) {
			throw new RuntimeException(e);
		}
	}
}
