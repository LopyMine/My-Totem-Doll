package net.lopymine.mtd.doll.data;

import java.util.Locale;
import lombok.Getter;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.network.chat.Component;

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
		return MyTotemDoll.text("modmenu.option.standard_doll_skin_type.result.%s".formatted(this.name().toLowerCase(Locale.ROOT)));
	}
}
