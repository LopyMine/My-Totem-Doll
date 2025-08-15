package net.lopymine.mtd.doll.data;

import lombok.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
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
	private TotemDollSprites sprites;
	@Nullable
	private TotemDollSprites frameTextures;

	@NotNull
	private TotemDollRenderProperties renderProperties = new TotemDollRenderProperties();

	public TotemDollData(@Nullable String nickname, @NotNull TotemDollSprites sprites) {
		this.renderProperties.refresh(sprites);
		this.renderProperties.setNickname(nickname);
		this.sprites = sprites;
	}

	public static TotemDollData create(@Nullable String nickname) {
		return new TotemDollData(nickname, TotemDollSprites.create());
	}

	public String getNickname() {
		return this.renderProperties.getNickname();
	}

	public void setStandardMModel(Identifier modelId) {
		BlockBenchModelManager.consumeModelById(modelId, this::setStandardMModel);
	}

	public void setStandardMModel(MModel model) {
		this.standardModel = new TotemDollModel(model, this.renderProperties.isSlim());
		this.renderProperties.setStandardMModel(model);
	}

	public void setFrameMModel(Identifier id) {
		this.renderProperties.consumeFrameMModel(id, this::setFrameMModel);
	}

	public void setFrameMModel(@Nullable MModel frameMModel) {
		this.renderProperties.setFrameMModel(frameMModel);

		TotemDollModel tempModel = this.getFrameModelBasedOnFrameMModel();
		if (tempModel != null && this.standardModel != null) {
			tempModel.setSlim(this.renderProperties.isSlim());
		}
	}

	@Nullable
	private TotemDollModel getFrameModelBasedOnFrameMModel() {
		if (this.renderProperties.getFrameMModel() != null) {
			if (this.frameModel == null || !this.frameModel.getMain().equals(this.renderProperties.getFrameMModel())) {
				return this.frameModel = new TotemDollModel(this.renderProperties.getFrameMModel(), this.renderProperties.isSlim());
			}
			return this.frameModel;
		}
		return null;
	}

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

	public TotemDollSprites getTexturesToRender() {
		return this.frameTextures == null ? this.sprites : this.frameTextures;
	}

	public TotemDollData copy() {
		return new TotemDollData(this.renderProperties.getNickname(), this.sprites.copy());
	}

	public void setSprites(@NotNull TotemDollSprites sprites) {
		this.sprites = sprites;
		if (this.standardModel == null) {
			return;
		}
		this.standardModel.setSlim(sprites.getArmsType().isSlim());
	}

	public void setFrameTextures(@Nullable TotemDollSprites frameTextures) {
		this.frameTextures = frameTextures;
		if (frameTextures == null) {
			return;
		}
		this.getModelToRender().setSlim(frameTextures.getArmsType().isSlim());
	}

	public void setFrameTextures(@Nullable AbstractClientPlayerEntity playerEntity) {
		if (playerEntity == null) {
			return;
		}

		//? if >=1.21 {
		net.minecraft.client.util.SkinTextures skinTextures = playerEntity.getSkinTextures();
		Identifier skinTexture = skinTextures.texture();
		Identifier capeTexture = skinTextures.capeTexture();
		Identifier elytraTexture = skinTextures.elytraTexture();
		boolean slim = skinTextures.model() == net.minecraft.client.util.SkinTextures.Model.SLIM;
		//?} else {
		/*Identifier skinTexture = playerEntity.getSkinTexture();
		Identifier capeTexture = playerEntity.getCapeTexture();
		Identifier elytraTexture = playerEntity.getElytraTexture();
		boolean slim = playerEntity.getModel().equalsIgnoreCase("slim");
		*///?}

		this.setFrameTextures(this.renderProperties.getPlayerSprites(skinTexture, capeTexture, elytraTexture, slim, true));
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

	public void clearFrameTextures() {
		this.frameTextures = null;
	}

	public TotemDollData refreshAndApplyRenderProperties() {
		return this.refreshRenderProperties().applyRenderProperties();
	}

	public TotemDollData refreshRenderProperties() {
		// Make sure it's cleared
		this.clearFrameModel();
		this.clearFrameTextures();
		this.renderProperties.refresh(this.sprites);
		this.getModelToRender().resetPartsVisibility();
		return this;
	}

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
