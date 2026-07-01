package net.lopymine.mtd.compat.sodium;

import net.lopymine.mtd.compat.CompatPlugin;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import org.spongepowered.asm.service.MixinService;

public class SodiumCompatPlugin extends CompatPlugin {

	private static final String HOT_SODIUM_VERSION = "0.6.0+mc1.21.1";

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
		String currentVersion = MyTotemDollLoader.getModVersion(this.getCompatModId(), true);
		boolean bl = MyTotemDollLoader.compareVersions(currentVersion, HOT_SODIUM_VERSION) < 0;
		MixinService.getService().getLogger("[MyTotemDoll: SodiumCompatPlugin]").info("[{}] Detected Sodium, current version older than hot: {}", mixinName, bl);
		return bl;
	}
}
