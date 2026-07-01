package net.lopymine.mtd.yacl.custom.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.YACLScreen;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;

@Getter
public class MyTotemDollYACLScreen extends YACLScreen {

	public MyTotemDollYACLScreen(YetAnotherConfigLib config, Screen parent) {
		super(config, parent);
	}

	@Override
	public void finishOrSave() {
		this.onClose();
	}

	@Override
	public void cancelOrReset() {
		OptionUtils.forEachOptions(this.config, Option::requestSetDefault);
	}

	@Override
	public void onClose() {
		super.finishOrSave();
		super.onClose();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

}
