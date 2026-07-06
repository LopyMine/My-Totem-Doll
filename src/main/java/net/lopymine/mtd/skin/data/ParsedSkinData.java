package net.lopymine.mtd.skin.data;

import lombok.*;

import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class ParsedSkinData {

	@Nullable
	private String skinUrl;
	@Nullable
	private String capeUrl;
	@Nullable
	private String elytraUrl;
	private boolean slim;

	public ParsedSkinData(@Nullable String skinUrl, @Nullable String capeUrl, @Nullable String elytraUrl, boolean slim) {
		this.skinUrl   = skinUrl;
		this.capeUrl   = capeUrl;
		this.slim      = slim;
		this.elytraUrl = elytraUrl;
	}
}
