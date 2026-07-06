package net.lopymine.mtd.tag;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.*;

@Getter
public class CustomModelTag extends Tag {

	private final ResourceLocation modelId;

	protected CustomModelTag(char tag, @Nullable TagAction action, ResourceLocation modelId) {
		super(tag, action);
		this.modelId = modelId;
	}

	public @NotNull String getModelName() {
		String path = this.modelId.getPath();
		int i = path.lastIndexOf('/');
		if (i != -1) {
			return path.substring(i + 1);
		}
		return path;
	}

	public static Builder startBuilder(char tag, ResourceLocation modelId) {
		return new Builder(tag, modelId);
	}

	public static class Builder {

		private final char tag;
		private final ResourceLocation modelId;
		private TagAction action;

		public Builder(char tag, ResourceLocation modelId) {
			this.tag = tag;
			this.modelId = modelId;
		}

		public Builder setAction(TagAction action) {
			this.action = action;
			return this;
		}

		public CustomModelTag build() {
			return new CustomModelTag(this.tag, this.action, this.modelId);
		}
	}
}
