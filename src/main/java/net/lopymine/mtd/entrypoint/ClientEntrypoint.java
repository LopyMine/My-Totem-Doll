package net.lopymine.mtd.entrypoint;

//? if fabric {

/*import net.fabricmc.api.ClientModInitializer;
import net.lopymine.mtd.client.MyTotemDollClient;

public class ClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MyTotemDollClient.onInitializeClient();
	}
}

*///?} elif neoforge {

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.modmenu.ModMenuIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = MyTotemDoll.MOD_ID, dist = Dist.CLIENT)
public class ClientEntrypoint {

	public ClientEntrypoint(ModContainer container) {
		MyTotemDollClient.onInitializeClient();
		new ModMenuIntegration().register(container);
	}

}

//?}
