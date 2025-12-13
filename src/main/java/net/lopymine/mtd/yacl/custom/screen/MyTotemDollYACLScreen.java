package net.lopymine.mtd.yacl.custom.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.*;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.lopymine.mtd.MyTotemDoll;

@Getter
public class MyTotemDollYACLScreen extends YACLScreen {

	public MyTotemDollYACLScreen(YetAnotherConfigLib config, Screen parent) {
		super(config, parent);
	}

	@Override
	public void finishOrSave() {
		this.close();
	}

	@Override
	public void cancelOrReset() {
		OptionUtils.forEachOptions(this.config, Option::requestSetDefault);
	}

	@Override
	public void close() {
		super.finishOrSave();
		super.close();
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

}
