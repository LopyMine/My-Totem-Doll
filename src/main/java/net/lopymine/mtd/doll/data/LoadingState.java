package net.lopymine.mtd.doll.data;

import lombok.Getter;
import net.minecraft.network.chat.Component;

import net.lopymine.mtd.MyTotemDoll;

@Getter
public enum LoadingState {

	ERROR, // Y
	CRITICAL_ERROR, // X
	NOT_FOUND, // X
	DESTROYED, // X
	NOT_DOWNLOADED, // Y
	WAITING_DOWNLOADING, // X
	DOWNLOADING, // X
	REGISTERING, // X
	DOWNLOADED; // X

	public Component getText() {
		return MyTotemDoll.text("modmenu.option.standard_doll_skin_type.result.%s".formatted(this.name().toLowerCase()));
	}
}
