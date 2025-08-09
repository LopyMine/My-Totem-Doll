package net.lopymine.mtd.doll.data;

import lombok.*;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.manager.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.config.totem.TotemDollArmsType;

import org.jetbrains.annotations.*;

@Getter
@Setter
@AllArgsConstructor
public class TotemDollSprites {

	@NotNull
	public static final AtlasSprite STEVE_SKIN_SPRITE = AtlasSprite.of(Identifier.of("minecraft", "textures/entity/player/wide/steve.png"));
	//? if >=1.21.2 {
	@NotNull
	public static final AtlasSprite ELYTRA_SPRITE = AtlasSprite.of(Identifier.of("textures/entity/equipment/wings/elytra.png"));
	//?} else {
	/*@NotNull
	public static final Identifier ELYTRA_TEXTURE = Identifier.of("minecraft","textures/entity/elytra.png");
	*///?}

	@NotNull
	private LoadingState state = LoadingState.NOT_DOWNLOADED;

	@Nullable
	private AtlasSprite skinSprite;
	@Nullable
	private AtlasSprite capeSprite;
	@Nullable
	private AtlasSprite elytraSprite;

	private TotemDollArmsType standardArmsType;
	private TotemDollArmsType armsType;

	public TotemDollSprites(@Nullable AtlasSprite skinSprite, @Nullable AtlasSprite capeSprite, @Nullable AtlasSprite elytraSprite, TotemDollArmsType armsType) {
		this.skinSprite   = skinSprite;
		this.capeSprite   = capeSprite;
		this.elytraSprite = elytraSprite;
		this.armsType     = armsType;
	}

	public static TotemDollSprites create() {
		return new TotemDollSprites(null, null, null, TotemDollArmsType.WIDE);
	}

	public static TotemDollSprites of(AbstractClientPlayerEntity player) {
		//? if >=1.21 {
		return of(player.getSkinTextures(), true);
		//?} else {
		/*Identifier capeTexture = PlayerSkinUtils.remapTextureIfRequired(player.getCapeTexture());
		TotemDollSprites totemDollTextures = new TotemDollSprites(player.getSkinTexture(), capeTexture, player.getElytraTexture(), TotemDollArmsType.of(player.getModel()));
		totemDollTextures.setState(LoadingState.DOWNLOADED);
		return totemDollTextures;
		*///?}
	}

	//? if >=1.21 {
	public static TotemDollSprites of(net.minecraft.client.util.SkinTextures skinTextures, boolean remapCape) {
		AtlasSprite tempCapeSprite = AtlasSprite.of(skinTextures.capeTexture());
		AtlasSprite capeSprite = remapCape && tempCapeSprite != null ? MyTotemDollAtlasSpriteManager.registerRemappedSprite(tempCapeSprite) : tempCapeSprite;
		AtlasSprite skinSprite = AtlasSprite.of(skinTextures.texture());
		AtlasSprite elytraSprite = AtlasSprite.of(skinTextures.elytraTexture());

		TotemDollSprites totemDollSprites = new TotemDollSprites(skinSprite, capeSprite, elytraSprite, TotemDollArmsType.of(skinTextures.model().getName()));
		totemDollSprites.setState(LoadingState.DOWNLOADED);
		return totemDollSprites;
	}
	//?}

	public void setStandardArmsType(TotemDollArmsType standardArmsType) {
		this.armsType = standardArmsType;
		this.standardArmsType = standardArmsType;
	}

	public TotemDollArmsType getArmsType() {
		return this.armsType == null ?
				this.standardArmsType == null ?
						TotemDollArmsType.WIDE
						:
						this.standardArmsType
				:
				this.armsType;
	}

	public AtlasSprite getSkinSprite() {
		return this.skinSprite == null || !this.skinSprite.isUploaded() || this.state != LoadingState.DOWNLOADED ? STEVE_SKIN_SPRITE : this.skinSprite;
	}

	public AtlasSprite getElytraSprite() {
		AtlasSprite capeSprite = this.getCapeSprite();
		if (capeSprite != null && capeSprite.isUploaded()) {
			return capeSprite;
		}
		AtlasSprite elytraSprite = this.elytraSprite;
		if (elytraSprite != null && elytraSprite.isUploaded()) {
			return elytraSprite;
		}
		return ELYTRA_SPRITE;
	}

	public void destroy() {
		this.setState(LoadingState.DESTROYED);

		AtlasSprite skinSprite = this.skinSprite;
		AtlasSprite capeSprite = this.capeSprite;
		AtlasSprite elytraSprite = this.elytraSprite;

		this.skinSprite   = null;
		this.capeSprite   = null;
		this.elytraSprite = null;

		if (skinSprite != null) {
			skinSprite.closeAndUnregisterAnyway();
		}

		if (capeSprite != null) {
			capeSprite.close();
		}

		if (elytraSprite != null) {
			elytraSprite.close();
		}
	}

	public boolean canStartDownloading() {
		return this.state == LoadingState.ERROR || this.state == LoadingState.NOT_DOWNLOADED;
	}

	public TotemDollSprites copy() {
		TotemDollSprites totemDollSprites = new TotemDollSprites(this.skinSprite, this.capeSprite, this.elytraSprite, this.armsType);
		totemDollSprites.setState(this.state);
		return totemDollSprites;
	}
}
