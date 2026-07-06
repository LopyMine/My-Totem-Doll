package net.lopymine.mtd.entrypoint;

//? if fabric {

import net.fabricmc.api.ClientModInitializer;
import net.lopymine.mtd.client.MyTotemDollClient;

public class ClientEntrypoint implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MyTotemDollClient.onInitializeClient();
	}
}

//?} elif forge {

/*import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.modmenu.ModMenuIntegration;
import net.minecraftforge.fml.ModLoadingContext;

public class ClientEntrypoint {

	public static void onInitializeClient() {
		MyTotemDollClient.onInitializeClient();
		new ModMenuIntegration().register(ModLoadingContext.get().getActiveContainer());
	}

}

*///?}
