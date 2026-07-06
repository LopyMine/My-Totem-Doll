package net.lopymine.mtd.config.totem;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;

import com.mojang.serialization.Codec;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.other.EnumWithText;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import org.jetbrains.annotations.Nullable;

@Getter
public enum TotemDollArmsType implements StringRepresentable, EnumWithText {

	WIDE,
	SLIM;

	public static final EnumCodec<TotemDollArmsType> CODEC = StringRepresentable.fromEnum(TotemDollArmsType::values);

	public Component getText() {
		return MyTotemDoll.text("modmenu.option.standard_doll_model_arms_type.%s".formatted(this.getSerializedName()));
	}

	public static TotemDollArmsType of(boolean slim) {
		return slim ? SLIM : WIDE;
	}

	public static TotemDollArmsType of(@Nullable String s) {
		if (s == null) {
			return WIDE;
		}
		return s.equals("slim") ? SLIM : WIDE;
	}

	@Override
	public String getSerializedName() {
		return this.name().toLowerCase();
	}

	public boolean isSlim() {
		return this == SLIM;
	}
}
