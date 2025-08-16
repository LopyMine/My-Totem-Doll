package net.lopymine.mtd.atlas.stitch;

import net.lopymine.mtd.atlas.AtlasSprite;

public interface OnSpriteUploaded {

	void onUploaded(AtlasSprite sprite);

	default OnSpriteUploaded then(OnSpriteUploaded then) {
		return (sprite) -> {
			this.onUploaded(sprite);
			then.onUploaded(sprite);
		};
	}
}
