package net.lopymine.mtd.modmenu.yacl.custom.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.TabExt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.Tab;

import net.lopymine.mtd.modmenu.yacl.custom.category.dummy.DummyCategoryTab;

public class MyTotemDollYACLScreen extends YACLScreen {

	public MyTotemDollYACLScreen(YetAnotherConfigLib config, Screen parent) {
		super(config, parent);
	}

	@Override
	public void finishOrSave() {
		super.finishOrSave();
		this.close();
	}

	@Override
	public void cancelOrReset() {
		OptionUtils.forEachOptions(this.config, Option::requestSetDefault);
	}

	@Override
	public void undo() {
		super.undo();
	}

	@Override
	public void close() {
		this.config.saveFunction().run();
		super.close();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	//? if =1.20.2 || =1.20.3 || =1.20 {

	/*@Override
	protected void init() {
		this.tabArea = new ScreenRect(0, 24 - 1, this.width, this.height - 24 + 1);
		super.init();
	}

	@Override
	public void renderBackground(DrawContext context /^? =1.20.2 || =1.20.3 {^//^, int mouseX, int mouseY, float delta ^//^?}^/) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) {
			this.renderBackgroundTexture(context);
		}
		Tab currentTab = this.tabManager.getCurrentTab();
		if (currentTab instanceof DummyCategoryTab dummyCategoryTab) {
			dummyCategoryTab.renderBackground(context);
		}
	}

	*///?}
}
