package net.lopymine.mtd.modmenu.yacl.simple;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.YetAnotherConfigLib.Builder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screen.Screen;

import net.lopymine.mtd.modmenu.yacl.custom.screen.BetterYACLScreenBuilder;
import net.lopymine.mtd.utils.ModMenuUtils;

import java.util.function.Consumer;

public class SimpleYACLScreenBuilder {

	private final Builder builder;
	private final Screen parent;

	public SimpleYACLScreenBuilder(Screen parent, Runnable onSave, Consumer<YACLScreen> onInit) {
		this.builder = BetterYACLScreenBuilder.startBuilder()
				.title(ModMenuUtils.getModTitle())
				.save(onSave)
				.screenInit(onInit);
		this.parent  = parent;
	}

	public static SimpleYACLScreenBuilder startBuilder(Screen parent, Runnable onSave) {
		return new SimpleYACLScreenBuilder(parent, onSave, (yaclScreen) -> {});
	}

	public static SimpleYACLScreenBuilder startBuilder(Screen parent, Runnable onSave, Consumer<YACLScreen> onInit) {
		return new SimpleYACLScreenBuilder(parent, onSave, onInit);
	}

	public SimpleYACLScreenBuilder categories(ConfigCategory... categories) {
		for (ConfigCategory category : categories) {
			if (category == null) {
				continue;
			}
			this.builder.category(category);
		}
		return this;
	}

	public Screen build() {
		return this.builder.build().generateScreen(this.parent);
	}
}
