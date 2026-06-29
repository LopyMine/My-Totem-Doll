package net.lopymine.mtd.entrypoint;

//? if fabric {
import net.lopymine.dl.DitheringLib;
import net.fabricmc.api.ModInitializer;

public class CommonEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		DitheringLib.onInitialize();
	}
}

//?} elif neoforge {

/*import net.lopymine.dl.DitheringLib;
import net.neoforged.fml.common.Mod;

@Mod(DitheringLib.MOD_ID)
public class CommonEntrypoint {

	public CommonEntrypoint() {
		DitheringLib.onInitialize();
	}

}

*///?}

