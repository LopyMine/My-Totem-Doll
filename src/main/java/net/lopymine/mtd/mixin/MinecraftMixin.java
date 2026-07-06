package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig.QuickPlayData;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.realmsclient.client.RealmsClient;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.lopymine.mtd.loader.MyTotemDollLoader;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.gui.screen.WelcomeScreen;

import java.util.List;
import java.util.function.Function;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setInitialScreen(Lcom/mojang/realmsclient/client/RealmsClient;Lnet/minecraft/server/packs/resources/ReloadInstance;Lnet/minecraft/client/main/GameConfig$QuickPlayData;)V"),
			//? if fabric {
			method = "<init>"
			//?} else {
			/*method = {"lambda$new$3", "lambda$new$4"}
			*///?}
	)
	private void addMTDHelloScreen(Minecraft client, RealmsClient realmsClient, ReloadInstance resourceReload, QuickPlayData quickPlay, Operation<Void> original) {
		Runnable runnable = () -> original.call(client, realmsClient, resourceReload, quickPlay);

		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (config.isFirstRun()) {
			client.setScreen(new WelcomeScreen(runnable));
			if (!MyTotemDollLoader.isDevelopmentEnvironment()) {
				config.setFirstRun(false);
			}
			config.save();
		} else {
			runnable.run();
		}
	}

}
