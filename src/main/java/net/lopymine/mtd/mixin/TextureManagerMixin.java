package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.llamalad7.mixinextras.sugar.Local;
import net.lopymine.mtd.MyTotemDoll;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

	//? if <=1.21.3 {
	/*@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V",
					remap = false
			),
			method = "loadTexture"
	)
	private void suppressMTDWarning(Logger instance, String s, Object a, Object o, Operation<Void> original) {
		if (!(a instanceof Identifier id)) {
			original.call(instance, s, a, o);
			return;
		}
		if (MyTotemDoll.MOD_ID.equals(id.getNamespace()) && id.getPath().startsWith("remapped_textures")) {
			return;
		}
		original.call(instance, s, a, o);
	}
	*///?} else {
	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;error(Ljava/lang/String;[Ljava/lang/Object;)V"
					, remap = false
			),
			method = "loadTexture(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/texture/ReloadableTexture;)Lnet/minecraft/client/texture/TextureContents;"
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
	//?}

}
