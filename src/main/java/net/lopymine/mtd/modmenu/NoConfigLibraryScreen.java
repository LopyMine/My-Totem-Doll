package net.lopymine.mtd.modmenu;

import com.google.common.collect.Sets;
import java.net.*;
import java.util.*;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.utils.ModMenuUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Util;
import org.jetbrains.annotations.*;

public class NoConfigLibraryScreen {

	private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet("http", "https");
	private static final String YACL_MODRINTH_LINK = "https://modrinth.com/mod/yacl/versions?l=fabric&g=";

	private NoConfigLibraryScreen() {
		throw new IllegalStateException("Screen class, use createScreen(...) method!");
	}

	@Contract("_ -> new")
	public static @NotNull Screen createScreen(Screen parent) {
		return new ConfirmScreen((open) -> NoConfigLibraryScreen.onConfirm(open, parent), ModMenuUtils.getModTitle(), ModMenuUtils.getNoConfigScreenMessage(), CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK);
	}

	private static void onConfirm(boolean open, Screen parent) {
		if (open) {
			try {
				String url = NoConfigLibraryScreen.YACL_MODRINTH_LINK + SharedConstants.getCurrentVersion().name();
				URI link = new URI(url);
				String string = link.getScheme();
				if (string == null) {
					throw new URISyntaxException(url, "Missing protocol");
				}
				if (!NoConfigLibraryScreen.ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
					throw new URISyntaxException(url, "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
				}
				Util.getPlatform().openUri(link);
			} catch (URISyntaxException e) {
				MyTotemDollClient.LOGGER.error("Can't open YACL Modrinth page:", e);
			}
		} else {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	public static Screen createScreenAboutOldVersion(Screen parent, String version) {
		return new ConfirmScreen((open) -> NoConfigLibraryScreen.onConfirm(open, parent), ModMenuUtils.getModTitle(), ModMenuUtils.getOldConfigScreenMessage(version), CommonComponents.GUI_CONTINUE, CommonComponents.GUI_BACK);
	}
}
