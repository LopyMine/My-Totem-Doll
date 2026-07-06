package net.lopymine.mtd.model.base;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class MModelCollection {

	private final List<MModel> models;
	private final String id;
	private int skipRendering = -1;
	private int visible = -1;

	public MModelCollection(List<MModel> models, String id) {
		this.models = models;
		this.id = id;
	}

	public boolean setVisible(boolean visible) {
		int state = visible ? 1 : 0;
		if (this.visible == state) {
			return false;
		}
		this.visible = state;
		for (MModel model : this.models) {
			model.visible = visible;
		}
		return true;
	}

	public void setSkipRendering(boolean skipRendering) {
		int state = skipRendering ? 1 : 0;
		if (this.skipRendering == state) {
			return;
		}
		this.skipRendering = state;
		for (MModel model : this.models) {
			model.setSkipRendering(skipRendering);
		}
	}

	public boolean isEmpty() {
		return this.models.isEmpty();
	}

	// DO NOT IMPLEMENT EQUALS AND HASHCODE HERE
}
