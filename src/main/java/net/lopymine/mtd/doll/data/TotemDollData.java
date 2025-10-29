package net.lopymine.mtd.doll.data;

import lombok.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;

//? if >=1.21.9 {
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.util.AssetInfo.TextureAsset;
import java.util.Optional;
//?} else {
/*import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.*;
*///?}
import net.minecraft.util.Identifier;

import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.model.base.MModel;

import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollData {

	private boolean shouldRecreateStandardModel;

	@Nullable
	private TotemDollModel standardModel;
	@Nullable
	private TotemDollModel frameModel;

	@NotNull
	private TotemDollRenderProperties renderProperties = new TotemDollRenderProperties();

	public TotemDollData(@Nullable String nickname, @NotNull TotemDollSprites sprites) {
		this.renderProperties.refresh(sprites);
		this.renderProperties.setNickname(nickname);
	}

	public TotemDollData(@NotNull TotemDollRenderProperties properties) {
		this.renderProperties.copyFrom(properties);
	}

	public static TotemDollData create(@Nullable String nickname) {
		return new TotemDollData(nickname, TotemDollSprites.create());
	}

	public TotemDollSprites getStandardSprites() {
		return this.renderProperties.getStandardSprites();
	}

	@Nullable
	public String getNickname() {
		return this.renderProperties.getNickname();
	}

	public void setStandardMModel(@NotNull Identifier modelId) {
		BlockBenchModelManager.consumeModelById(modelId, this::setStandardMModel);
	}

	public void setStandardMModel(@Nullable MModel model) {
		this.renderProperties.setStandardMModel(model);
		if (model == null) {
			return;
		}
		this.standardModel = this.renderProperties.createStandardModel();
	}

	public void setFrameMModel(@NotNull Identifier id) {
		this.renderProperties.consumeFrameMModel(id, this::setFrameMModel);
	}

	public void setFrameMModel(@Nullable MModel frameMModel) {
		this.renderProperties.setFrameMModel(frameMModel);
	}

	@Nullable
	private TotemDollModel getFrameModelBasedOnFrameMModel() {
		if (this.renderProperties.getFrameMModel() != null) {
			if (this.frameModel == null || !this.frameModel.getMain().equals(this.renderProperties.getFrameMModel())) {
				return this.frameModel = this.renderProperties.createFrameModel();
			}
			return this.frameModel;
		}
		return null;
	}

	public void clearAllFrameModelsCompletely() {
		this.clearFrameModel();
		this.renderProperties.clearCachedFrameMModels();
	}

	public void clearFrameModel() {
		if (this.frameModel != null) {
			this.frameModel.resetPartsVisibility();
			this.frameModel = null;
		}
	}

	public void clearFrameSprites() {
		this.renderProperties.setFrameSprites(null);
	}

	@NotNull
	public TotemDollModel getModelToRender() {
		TotemDollModel tempModel = this.getFrameModelBasedOnFrameMModel();
		if (tempModel != null) {
			return tempModel;
		}

		if (this.standardModel != null && !this.shouldRecreateStandardModel) {
			return this.standardModel;
		}

		this.setStandardMModel(TotemDollModel.createDollModel());

		if (this.shouldRecreateStandardModel) {
			this.shouldRecreateStandardModel = false;
		}

		return this.standardModel;
	}

	@NotNull
	public TotemDollSprites getSpritesToRender() {
		return this.renderProperties.getFrameSprites() == null ? this.renderProperties.getStandardSprites() : this.renderProperties.getFrameSprites();
	}

	public void setSprites(@NotNull TotemDollSprites sprites) {
		this.renderProperties.setStandardSprites(sprites);
	}

	@SuppressWarnings("unused")
	public void setFrameSprites(@Nullable TotemDollSprites frameSprites) {
		this.renderProperties.setFrameSprites(frameSprites);
	}

	public void setFrameSprites(@Nullable AbstractClientPlayerEntity playerEntity) {
		if (playerEntity == null) {
			return;
		}

		//? if >=1.21.9 {
		SkinTextures skinTextures = playerEntity.getSkin();
		Identifier skinTexture = skinTextures.body().texturePath();
		Identifier capeTexture = Optional.of(skinTextures).map(SkinTextures::cape).map(TextureAsset::texturePath).orElse(null);
		Identifier elytraTexture = Optional.of(skinTextures).map(SkinTextures::cape).map(TextureAsset::texturePath).orElse(null);
		boolean slim = skinTextures.model() == PlayerSkinType.SLIM;
		//?} elif >=1.21 {
		/*SkinTextures skinTextures = playerEntity.getSkinTextures();
		Identifier skinTexture = skinTextures.texture();
		Identifier capeTexture = skinTextures.capeTexture();
		Identifier elytraTexture = skinTextures.elytraTexture();
		boolean slim = skinTextures.model() == SkinTextures.Model.SLIM;
		*///?} else {
		/*Identifier skinTexture = playerEntity.getSkinTexture();
		Identifier capeTexture = playerEntity.getCapeTexture();
		Identifier elytraTexture = playerEntity.getElytraTexture();
		boolean slim = playerEntity.getModel().equalsIgnoreCase("slim");
		*///?}

		this.renderProperties.setFrameSprites(skinTexture, capeTexture, elytraTexture, slim, true);
	}

	@NotNull
	public TotemDollData copy() {
		return new TotemDollData(this.renderProperties);
	}

	@NotNull
	public TotemDollData refreshAndApplyRenderProperties() {
		return this.refreshRenderProperties().applyRenderProperties();
	}

	@NotNull
	public TotemDollData refreshRenderProperties() {
		// Make sure it's cleared
		this.clearFrameModel();
		this.clearFrameSprites();
		this.getModelToRender().resetPartsVisibility();
		this.renderProperties.refresh();
		return this;
	}

	@NotNull
	public TotemDollData applyRenderProperties() {
		this.renderProperties.applyToModel(this.getModelToRender());
		return this;
	}

	//? if >=1.21.6 {
	@NotNull
	public net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer getGuiRenderer(net.minecraft.client.render.VertexConsumerProvider.Immediate immediate) {
		return net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer.getRenderer(this.renderProperties, immediate);
	}
	//?}
}
