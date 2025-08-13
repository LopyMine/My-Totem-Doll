package net.lopymine.mtd.doll.data;

import java.util.Objects;
import lombok.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
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
	private TotemDollSprites textures;
	@Nullable
	private TotemDollSprites frameTextures;

	@NotNull
	private TotemDollRenderProperties renderProperties = new TotemDollRenderProperties();

	public TotemDollData(@Nullable String nickname, @NotNull TotemDollSprites textures) {
		this.renderProperties.refresh(textures);
		this.renderProperties.setNickname(nickname);
		this.textures = textures;
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
		return this.frameTextures == null ? this.textures : this.frameTextures;
	}

	public TotemDollData copy() {
		return new TotemDollData(this.renderProperties.getNickname(), this.textures.copy());
	}

	public void setTextures(@NotNull TotemDollSprites textures) {
		this.textures = textures;
		if (this.standardModel == null) {
			return;
		}
		this.standardModel.setSlim(textures.getArmsType().isSlim());
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
		SkinTextures skinTextures = playerEntity.getSkinTextures();
		//?} else {
		/*AbstractClientPlayerEntity skinTextures = playerEntity;
		*///?}

		Identifier skinTexture = skinTextures.texture();
		Identifier capeTexture = skinTextures.capeTexture();
		Identifier elytraTexture = skinTextures.elytraTexture();
		boolean slim = skinTextures.model() == Model.SLIM;

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
		this.renderProperties.refresh(this.textures);
		this.getModelToRender().resetPartsVisibility();
		return this;
	}

	public TotemDollData applyRenderProperties() {
		this.renderProperties.applyToModel(this.getModelToRender());
		return this;
	}

	//? if >=1.21.6 {
	@NotNull
	public net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer getGuiRenderer(Immediate immediate) {
		return net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer.getRenderer(this.renderProperties, immediate);
	}
	//?}
}
