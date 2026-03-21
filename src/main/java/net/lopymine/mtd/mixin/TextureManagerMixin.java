package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;error(Ljava/lang/String;[Ljava/lang/Object;)V"
					, remap = false
			),
			method = "loadContentsSafe(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/renderer/texture/ReloadableTexture;)Lnet/minecraft/client/renderer/texture/TextureContents;"
	)
	private void suppressMTDWarning(Logger instance, String s, Object[] objects, Operation<Void> original, @Local(argsOnly = true) Identifier id) {
		if (id == null) {
			return;
		}
		if (MyTotemDoll.MOD_ID.equals(id.getNamespace()) && id.getPath().startsWith("remapped_textures")) {
			return;
		}
		original.call(instance, s, objects);
	}

}
