package net.lopymine.mtd.tag;

import lombok.Getter;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.extension.IdentifierExtension;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.*;

@Getter
public class CustomModelTag extends Tag {

	private final Identifier id;
	private final Identifier modelId;
	private final boolean animated;

	protected CustomModelTag(char tag, @Nullable TagAction action, Identifier id, Identifier modelId, boolean animated) {
		super(tag, action);
		this.id       = id;
		this.modelId  = modelId;
		this.animated = animated;
	}

	public static Builder startBuilder(char tag, Identifier modelId) {
		return new Builder(tag, modelId, modelId, false);
	}

	public static Builder startAnimatedBuilder(char tag, Identifier configId, Identifier modelId) {
		return new Builder(tag, configId, modelId, true);
	}

	@Override
	public void process(TotemDollData data) {
		if (!this.animated) {
			data.getRenderProperties().setAnimatedConfigId(null);
		}
		super.process(data);
	}

	public @NotNull String getModelName() {
		return IdentifierExtension.getFileName(this.id);
	}

	public static class Builder {

		private final char tag;
		private final Identifier id;
		private final Identifier modelId;
		private final boolean animated;
		private TagAction action;

		public Builder(char tag, Identifier id, Identifier modelId, boolean animated) {
			this.tag      = tag;
			this.id       = id;
			this.modelId  = modelId;
			this.animated = animated;
		}

		public Builder setAction(TagAction action) {
			this.action = action;
			return this;
		}

		public CustomModelTag build() {
			return new CustomModelTag(this.tag, this.action, this.id, this.modelId, this.animated);
		}
	}
}
