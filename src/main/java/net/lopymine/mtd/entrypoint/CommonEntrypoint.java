package net.lopymine.mtd.entrypoint;

//? if fabric {

import net.fabricmc.api.ModInitializer;
import net.lopymine.mtd.MyTotemDoll;

public class CommonEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		MyTotemDoll.onInitialize();
	}
}

//?} elif forge {

/*import net.lopymine.mtd.MyTotemDoll;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(MyTotemDoll.MOD_ID)
public class CommonEntrypoint {

	public CommonEntrypoint() {
		MyTotemDoll.onInitialize();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEntrypoint::onInitializeClient);
	}

}

*///?}
