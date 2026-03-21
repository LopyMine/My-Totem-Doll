package net.lopymine.mtd.doll.data;

import it.unimi.dsi.fastutil.ints.*;
import java.util.*;
import java.util.function.Consumer;
import lombok.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollRenderProperties {

	@NotNull
	private final Map<Identifier, MModel> cachedFrameMModels = new HashMap<>();
	@NotNull
	private final Int2ObjectMap<TotemDollSprites> cachedFrameTextures = new Int2ObjectArrayMap<>();
	private boolean slim;
	@Nullable
	private String nickname;
	@Nullable
	private DollRenderContext renderContext;
	@NotNull
	private String[] disabledParts = new String[0];
	@NotNull
	private String[] enabledParts = new String[0];
	@Nullable
	private MModel standardMModel;
	@Nullable
	private MModel frameMModel;
	@NotNull
	private TotemDollSprites standardSprites;
	@Nullable
	private TotemDollSprites frameSprites;

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TotemDollRenderProperties that)) return false;
		return this.isSlim() == that.isSlim() && this.getRenderContext() == that.getRenderContext() && Objects.equals(this.getNickname(), that.getNickname()) && Objects.deepEquals(this.getDisabledParts(), that.getDisabledParts()) && Objects.deepEquals(this.getEnabledParts(), that.getEnabledParts()) && Objects.equals(this.getFrameMModel(), that.getFrameMModel()) && Objects.equals(this.getStandardMModel(), that.getStandardMModel());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.isSlim(), this.getRenderContext(), this.getNickname(), Arrays.hashCode(this.getDisabledParts()), Arrays.hashCode(this.getEnabledParts()), this.getFrameMModel(), this.getStandardMModel());
	}

	public TotemDollModel createStandardModel() {
		return new TotemDollModel(this.standardMModel, this.isSlim());
	}

	public TotemDollModel createFrameModel() {
		return new TotemDollModel(this.frameMModel, this.isSlim());
	}

	public void consumeFrameMModel(@NotNull Identifier id, Consumer<MModel> set) {
		MModel model = this.cachedFrameMModels.get(id);
		if (model == null) {
			BlockBenchModelManager.getModelAsyncAsResponse(id, (response) -> {
				if (!response.isEmpty()) {
					MModel tempMModel = response.value();
					this.cachedFrameMModels.put(id, tempMModel);
					set.accept(tempMModel);
				}
			});
			return;
		}
		set.accept(model);
	}

	public void setFrameSprites(Identifier skinTexture, Identifier capeTexture, Identifier elytraTexture, boolean slim, boolean remapCape) {
		int hash = Objects.hash(skinTexture, capeTexture, elytraTexture, slim);
		TotemDollSprites cachedSprites = this.cachedFrameTextures.get(hash);
		if (cachedSprites == null) {
			TotemDollSprites sprites = TotemDollSprites.of(skinTexture, capeTexture, elytraTexture, slim, remapCape);
			this.cachedFrameTextures.put(hash, sprites);
			this.setFrameSprites(sprites);
		}
		this.setFrameSprites(cachedSprites);
	}

	public void disable(MModelCollection collection) {
		if (!collection.setVisible(false)) {
			return;
		}
		String[] created = Arrays.copyOf(this.disabledParts, this.disabledParts.length + 1);
		created[created.length - 1] = collection.getId();
		this.disabledParts          = created;
	}

	public void enable(MModelCollection collection) {
		if (!collection.setVisible(true)) {
			return;
		}
		String[] created = Arrays.copyOf(this.enabledParts, this.enabledParts.length + 1);
		created[created.length - 1] = collection.getId();
		this.enabledParts           = created;
	}

	public void refresh() {
		this.refresh(this.standardSprites);
	}

	public void refresh(TotemDollSprites sprites) {
		this.standardSprites = sprites;
		this.enabledParts    = new String[0];
		this.disabledParts   = new String[0];
		this.slim            = sprites.getArmsType().isSlim();
		this.frameMModel     = null;
		this.renderContext   = null;
	}

	public void clearCachedFrameMModels() {
		this.cachedFrameMModels.clear();
		this.frameMModel = null;
	}

	public void applyToModel(TotemDollModel model) {
		for (String part : this.disabledParts) {
			MModelCollection collection = model.getCollectionOfPart(part);
			if (collection == null) {
				continue;
			}
			TotemDollModel.disableIfPresent(collection);
		}
		for (String part : this.enabledParts) {
			MModelCollection collection = model.getCollectionOfPart(part);
			if (collection == null) {
				continue;
			}
			TotemDollModel.enableIfPresent(collection);
		}
		model.setSlim(this.isSlim());
	}

	public TotemDollRenderProperties copyFrom(TotemDollRenderProperties properties) {
		this.setSlim(properties.isSlim());
		this.setRenderContext(properties.getRenderContext());
		this.setNickname(properties.getNickname());
		this.setDisabledParts(properties.getDisabledParts().clone());
		this.setEnabledParts(properties.getEnabledParts().clone());
		this.setStandardMModel(properties.getStandardMModel());
		this.setFrameMModel(properties.getFrameMModel());
		this.setStandardSprites(properties.getStandardSprites());
		this.setFrameSprites(properties.getFrameSprites());
		return this;
	}

	public TotemDollRenderProperties copy() {
		return new TotemDollRenderProperties().copyFrom(this);
	}
}
