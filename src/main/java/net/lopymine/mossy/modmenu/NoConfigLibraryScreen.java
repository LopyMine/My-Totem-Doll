package net.lopymine.mossy.modmenu;

import com.google.common.collect.Sets;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import net.lopymine.mossy.client.MossyClient;

import java.net.*;
import java.util.*;
import org.jetbrains.annotations.*;

public class NoConfigLibraryScreen {

	private NoConfigLibraryScreen() {
		throw new IllegalStateException("Screen class, use createScreen(...) method!");
	}

	private static final Text TITLE = Text.translatable("mossy.modmenu.title");
	private static final Text MESSAGE = Text.translatable("mossy.modmenu.no_config_library_screen.message");
	private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
	private static final String YACL_MODRINTH_LINK = "https://modrinth.com/mod/yacl/versions?l=fabric&g=";

	@Contract("_ -> new")
	public static @NotNull Screen createScreen(Screen parent) {
		return new ConfirmScreen((open) -> NoConfigLibraryScreen.onConfirm(open, parent), NoConfigLibraryScreen.TITLE, NoConfigLibraryScreen.MESSAGE, ScreenTexts.CONTINUE, ScreenTexts.BACK);
	}

	private static void onConfirm(boolean open, Screen parent) {
		if (open) {
			try {
				String url = NoConfigLibraryScreen.YACL_MODRINTH_LINK + SharedConstants.getGameVersion().getName();
				URI link = new URI(url);
				String string = link.getScheme();
				if (string == null) {
					throw new URISyntaxException(url, "Missing protocol");
				}
				if (!NoConfigLibraryScreen.ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
					throw new URISyntaxException(url, "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
				}
				Util.getOperatingSystem().open(link);
			} catch (URISyntaxException e) {
				MossyClient.LOGGER.error("Can't open YACL Modrinth page:", e);
			}
		} else {
			MinecraftClient.getInstance().setScreen(parent);
		}
	}
}
