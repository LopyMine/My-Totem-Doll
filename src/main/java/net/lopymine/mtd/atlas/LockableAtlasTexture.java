package net.lopymine.mtd.atlas;

import lombok.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class LockableAtlasTexture {

	@NotNull
	private SpriteAtlasTexture atlas;
	private boolean locked;
	private Runnable unlockHook;

	public LockableAtlasTexture(@NotNull SpriteAtlasTexture atlas) {
		this.atlas = atlas;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		if (!locked && this.unlockHook != null) {
			this.unlockHook.run();
		}
	}
}
