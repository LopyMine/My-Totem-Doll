package net.lopymine.mtd.yacl.custom.simple;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib.Builder;
import dev.isxander.yacl3.gui.YACLScreen;
import java.util.function.Consumer;
import net.lopymine.mtd.utils.ModMenuUtils;
import net.lopymine.mtd.utils.mixin.yacl.BetterYACLScreenBuilder;
import net.minecraft.client.gui.screens.Screen;

public class SimpleYACLScreen {

	private final Builder builder;
	private final Screen parent;

	public SimpleYACLScreen(Screen parent, Runnable onSave, Consumer<YACLScreen> onInit) {
		this.builder = BetterYACLScreenBuilder.startBuilder()
				.title(ModMenuUtils.getModTitle())
				.save(onSave)
				.screenInit(onInit);
		this.parent  = parent;
	}

	public static SimpleYACLScreen startBuilder(Screen parent, Runnable onSave) {
		return new SimpleYACLScreen(parent, onSave, (yaclScreen) -> {});
	}

	public static SimpleYACLScreen startBuilder(Screen parent, Runnable onSave, Consumer<YACLScreen> onInit) {
		return new SimpleYACLScreen(parent, onSave, onInit);
	}

	public SimpleYACLScreen categories(ConfigCategory... categories) {
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
