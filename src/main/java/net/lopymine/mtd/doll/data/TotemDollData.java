package net.lopymine.mtd.doll.data;

import java.util.Optional;
import lombok.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer;
import net.lopymine.mtd.model.base.MModel;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.*;
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

	public void setFrameSprites(@Nullable AbstractClientPlayer playerEntity) {
		if (playerEntity == null) {
			return;
		}

		PlayerSkin skinTextures = playerEntity.getSkin();
		Identifier skinTexture = skinTextures.body().texturePath();
		Identifier capeTexture = Optional.of(skinTextures).map(PlayerSkin::cape).map(Texture::texturePath).orElse(null);
		Identifier elytraTexture = Optional.of(skinTextures).map(PlayerSkin::cape).map(Texture::texturePath).orElse(null);
		boolean slim = skinTextures.model() == PlayerModelType.SLIM;

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


	@NotNull
	public TotemDollGuiElementRenderer getGuiRenderer(net.minecraft.client.renderer.MultiBufferSource.BufferSource immediate) {
		return TotemDollGuiElementRenderer.getRenderer(this.renderProperties, immediate);
	}

}
