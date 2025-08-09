package net.lopymine.mtd.doll.data;

import java.util.*;
import java.util.function.Consumer;
import lombok.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

@Getter
@Setter
public class TotemDollRenderProperties {

	private boolean slim;
	@Nullable
	private DollRenderContext renderContext;
	@Nullable
	private String nickname;
	@NotNull
	private String[] disabledParts = new String[0];
	@NotNull
	private String[] enabledParts = new String[0];
	@Nullable
	private MModel standardMModel;
	@Nullable
	private MModel frameMModel;
	@NotNull
	private final Map<Identifier, MModel> cachedFrameMModels = new HashMap<>();

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TotemDollRenderProperties that)) return false;
		return this.isSlim() == that.isSlim() && this.getRenderContext() == that.getRenderContext() && Objects.equals(this.getNickname(), that.getNickname()) && Objects.deepEquals(this.getDisabledParts(), that.getDisabledParts()) && Objects.deepEquals(this.getEnabledParts(), that.getEnabledParts()) && Objects.equals(this.getFrameMModel(), that.getFrameMModel()) && Objects.equals(this.getStandardMModel(), that.getStandardMModel());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.isSlim(), this.getRenderContext(), this.getNickname(), Arrays.hashCode(this.getDisabledParts()), Arrays.hashCode(this.getEnabledParts()), this.getFrameMModel(), this.getStandardMModel());
	}

	public void consumeFrameMModel(Identifier id, Consumer<MModel> set) {
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

	public void disable(MModelCollection collection) {
		if (!collection.setVisible(false)) {
			return;
		}
		String[] created = Arrays.copyOf(this.disabledParts, this.disabledParts.length + 1);
		created[created.length-1] = collection.getId();
		this.disabledParts = created;
	}

	public void enable(MModelCollection collection) {
		if (!collection.setVisible(true)) {
			return;
		}
		String[] created = Arrays.copyOf(this.enabledParts, this.enabledParts.length + 1);
		created[created.length-1] = collection.getId();
		this.enabledParts = created;
	}

	public void refresh(TotemDollSprites textures) {
		this.enabledParts = new String[0];
		this.disabledParts = new String[0];
		this.slim        = textures.getArmsType().isSlim();
		this.frameMModel = null;
		this.renderContext = null;
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

	public TotemDollRenderProperties copy() {
		TotemDollRenderProperties copy = new TotemDollRenderProperties();
		copy.setSlim(this.isSlim());
		copy.setRenderContext(this.getRenderContext());
		copy.setNickname(this.getNickname());
		copy.setDisabledParts(this.getDisabledParts().clone());
		copy.setEnabledParts(this.getEnabledParts().clone());
		copy.setStandardMModel(this.getStandardMModel());
		copy.setFrameMModel(this.getFrameMModel());
		return copy;
	}
}
