package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.*;
import net.minecraft.client.RunArgs.QuickPlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.resource.ResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import net.fabricmc.loader.api.FabricLoader;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.gui.screen.WelcomeScreen;

import java.util.List;
import java.util.function.Function;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	//? if >=1.21 {
	@Inject(at = @At("HEAD"), method = "createInitScreens")
	private void addMTDHelloScreen(List<Function<Runnable, Screen>> list, /*? if >=1.21.6 {*/ CallbackInfoReturnable<Boolean> /*?} else {*/ /*CallbackInfo *//*?}*/ ci) {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (config.isFirstRun()) {
			list.add(WelcomeScreen::new);
			if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
				config.setFirstRun(false);
			}
			config.save();
		}
	}
	//?} else {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onInitFinished(Lnet/minecraft/client/realms/RealmsClient;Lnet/minecraft/resource/ResourceReload;Lnet/minecraft/client/RunArgs$QuickPlay;)V"), method = "<init>")
	private void addMTDHelloScreen(MinecraftClient client, RealmsClient realmsClient, ResourceReload resourceReload, QuickPlay quickPlay, Operation<Void> original) {
		Runnable runnable = () -> original.call(client, realmsClient, resourceReload, quickPlay);

		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (config.isFirstRun()) {
			client.setScreen(new WelcomeScreen(runnable));
			if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
				config.setFirstRun(false);
			}
			config.save();
		} else {
			runnable.run();
		}
	}
	*///?}

}
