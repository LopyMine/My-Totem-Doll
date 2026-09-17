package net.lopymine.mtd.doll.data;

import java.util.*;
import lombok.*;
import net.lopymine.mtd.config.resourcepack.AnimatedDollConfig;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.special.TotemDollGuiElementRenderer;
import net.lopymine.mtd.model.base.MModel;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.lopymine.mtd.pack.manager.AnimatedDollConfigsManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.*;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollData {

	private final Map<Identifier, TotemDollModel> cachedFrameModels = new HashMap<>();

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

	public void setAnimatedDoll(@NotNull Identifier configId) {
		AnimatedDollConfig config = AnimatedDollConfigsManager.getRegisteredConfigs().get(configId);
		if (config == null) {
			return;
		}

		this.renderProperties.setAnimatedConfigId(configId);

		Identifier modelId = config.getStandardModelId();
		if (modelId != null) {
			this.setFrameMModel(modelId);
		}
	}

	public void setFrameMModel(@Nullable MModel frameMModel) {
		this.renderProperties.setFrameMModel(frameMModel);
	}

	@Nullable
	private TotemDollModel getFrameModelBasedOnFrameMModel() {
		MModel frameMModel = this.renderProperties.getFrameMModel();
		if (frameMModel == null) {
			return null;
		}

		if (this.frameModel != null && this.frameModel.getMain().equals(frameMModel)) {
			return this.frameModel;
		}

		Identifier location = frameMModel.getLocation();
		TotemDollModel cachedModel = location == null ? null : this.cachedFrameModels.get(location);
		if (cachedModel != null && cachedModel.getMain().equals(frameMModel)) {
			return this.frameModel = cachedModel;
		}

		TotemDollModel createdModel = this.renderProperties.createFrameModel();
		if (location != null) {
			this.cachedFrameModels.put(location, createdModel);
		}

		return this.frameModel = createdModel;
	}

	public void clearAllFrameModelsCompletely() {
		this.clearFrameModel();
		this.cachedFrameModels.clear();
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
	public TotemDollGuiElementRenderer createGuiRenderer() {
		return TotemDollGuiElementRenderer.createGuiRenderer(this.renderProperties);
	}
}
