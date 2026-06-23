package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;clearTextureData()V"), method = "close")
	private void noNoNo(TextureAtlas atlas, Operation<Void> original) {
		if (atlas.location().equals(MyTotemDollAtlasManager.ATLAS_ID)) {
			return;
		}
		original.call(atlas);
	}

}
