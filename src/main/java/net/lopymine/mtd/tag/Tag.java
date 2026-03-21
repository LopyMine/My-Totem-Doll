package net.lopymine.mtd.tag;

import lombok.*;
import net.lopymine.mtd.doll.data.TotemDollData;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

	@Getter
	private final char tag;
	@Nullable
	private TagAction action;

	private Tag(char tag) {
		this.tag = tag;
	}

	public static Builder startBuilder(char character) {
		return new Builder(character);
	}

	public static Tag simple(char c) {
		return new Tag(c);
	}

	public void process(TotemDollData data) {
		if (this.action == null) {
			return;
		}
		this.action.process(data);
	}

	public static class Builder {

		private final char tag;
		private TagAction action;

		public Builder(char tag) {
			this.tag = tag;
		}

		public Builder setAction(TagAction action) {
			this.action = action;
			return this;
		}

		public Tag build() {
			return new Tag(this.tag, this.action);
		}
	}
}
