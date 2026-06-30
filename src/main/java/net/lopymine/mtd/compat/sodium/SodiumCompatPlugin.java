package net.lopymine.mtd.compat.sodium;

import net.lopymine.mtd.compat.CompatPlugin;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import org.spongepowered.asm.service.MixinService;

public class SodiumCompatPlugin extends CompatPlugin {

	@Override
	protected String getCompatModId() {
		return "sodium";
	}

}
