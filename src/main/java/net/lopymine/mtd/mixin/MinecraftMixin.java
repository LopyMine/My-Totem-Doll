package net.lopymine.mtd.mixin;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.loader.api.FabricLoader;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.gui.screen.WelcomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(at = @At("HEAD"), method = "addInitialScreens")
	private void addMTDHelloScreen(List<Function<Runnable, Screen>> list, CallbackInfoReturnable<Boolean> ci) {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (config.isFirstRun() || config.isFirstRunTemp()) {
			list.add(WelcomeScreen::new);
			if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
				config.setFirstRun(false);
				config.setFirstRunTemp(false);
			}
			config.save();
		}
	}

}
