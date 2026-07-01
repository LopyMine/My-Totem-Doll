package net.lopymine.mtd.mixin;

import java.util.List;
import java.util.function.Function;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.gui.screen.WelcomeScreen;
import net.lopymine.mtd.loader.MyTotemDollLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Inject(at = @At("HEAD"), method = "addInitialScreens")
	private void addMTDHelloScreen(List<Function<Runnable, Screen>> list, CallbackInfo ci) {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (config.isFirstRun()) {
			list.add(WelcomeScreen::new);
			if (!MyTotemDollLoader.isDevelopmentEnvironment()) {
				config.setFirstRun(false);
			}
			config.save();
		}
	}

}
