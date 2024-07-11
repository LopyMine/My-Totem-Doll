package net.lopymine.mtd.utils;

import lombok.Getter;
import net.minecraft.util.Identifier;

import net.fabricmc.api.*;

import org.jetbrains.annotations.Nullable;

public record SkinTextures(Identifier texture, @Nullable String textureUrl, @Nullable Identifier capeTexture, @Nullable Identifier elytraTexture, Model model, boolean secure) {

	public SkinTextures(Identifier texture, @Nullable String textureUrl, @Nullable Identifier capeTexture, @Nullable Identifier elytraTexture, Model model, boolean secure) {
		this.texture = texture;
		this.textureUrl = textureUrl;
		this.capeTexture = capeTexture;
		this.elytraTexture = elytraTexture;
		this.model = model;
		this.secure = secure;
	}

	public Identifier texture() {
		return this.texture;
	}

	@Nullable
	public String textureUrl() {
		return this.textureUrl;
	}

	@Nullable
	public Identifier capeTexture() {
		return this.capeTexture;
	}

	@Nullable
	public Identifier elytraTexture() {
		return this.elytraTexture;
	}

	public Model model() {
		return this.model;
	}

	public boolean secure() {
		return this.secure;
	}

	@Getter
	@Environment(EnvType.CLIENT)
	public enum Model {
		SLIM("slim"),
		WIDE("default");

		private final String name;

		Model(String name) {
			this.name = name;
		}

		public static Model fromName(@Nullable String name) {
			if (name == null) {
				return WIDE;
			} else {
				Model model;
				if (name.equals("slim")) {
					model = SLIM;
				} else {
					model = WIDE;
				}

				return model;
			}
		}

	}
}

