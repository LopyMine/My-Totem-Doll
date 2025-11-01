package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import net.minecraft.client.texture.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin {

	@Unique
	private static final String TEXT = "Wait! This crash was caused by the \"my-totem-doll\" mod SPECIFICALLY to prevent a crash via drivers. This crash was made to make debugging this unexpected error easier. Someone (maybe \"my-totem-doll\") just pushed closed sprite to upload and this shouldn't happen! Please report this crash-report to \"my-totem-doll\" issue tracker: https://github.com/LopyMine/My-Totem-Doll/issues";

	//? if >=1.21.6 {
	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/CommandEncoder;writeToTexture(Lcom/mojang/blaze3d/textures/GpuTexture;Lnet/minecraft/client/texture/NativeImage;IIIIIIII)V"
			),
			method = "upload(IIII[Lnet/minecraft/client/texture/NativeImage;Lcom/mojang/blaze3d/textures/GpuTexture;)V"
	)
	private void validateImageBeforeUpload(com.mojang.blaze3d.systems.CommandEncoder instance, com.mojang.blaze3d.textures.GpuTexture target, NativeImage source, int mipLevel, int depth, int offsetX, int offsetY, int width, int height, int skipPixels, int skipRows, Operation<Void> original) {
		if (source.pointer == 0L) {
			throw new IllegalArgumentException(TEXT);
		}
		original.call(instance, target, source, mipLevel, depth, offsetX, offsetY, width, height, skipPixels, skipRows);
	}
	//?} elif >=1.21.5 {
	/*@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/CommandEncoder;writeToTexture(Lcom/mojang/blaze3d/textures/GpuTexture;Lnet/minecraft/client/texture/NativeImage;IIIIIII)V"
			),
			method = "upload(IIII[Lnet/minecraft/client/texture/NativeImage;Lcom/mojang/blaze3d/textures/GpuTexture;)V"
	)
	private void validateImageBeforeUpload(com.mojang.blaze3d.systems.CommandEncoder instance, com.mojang.blaze3d.textures.GpuTexture target, NativeImage source, int mipLevel, int intoX, int intoY, int width, int height, int x, int y, Operation<Void> original) {
		if (source.pointer == 0L) {
			throw new IllegalArgumentException(TEXT);
		}
		original.call(instance, target, source, mipLevel, intoX, intoY, width, height, x, y);
	}
	*///?} elif >=1.21.4 {
	/*@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/texture/NativeImage;upload(IIIIIIIZ)V"
			),
			method = "upload(IIII[Lnet/minecraft/client/texture/NativeImage;)V"
	)
	private void validateImageBeforeUpload(NativeImage instance, int level, int offsetX, int offsetY, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean blur, Operation<Void> original) {
		if (instance.pointer == 0L) {
			throw new IllegalArgumentException(TEXT);
		}
		original.call(instance, level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, blur);
	}
	*///?} else {
	/*@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/texture/NativeImage;upload(IIIIIIIZZ)V"
			),
			method = "upload(IIII[Lnet/minecraft/client/texture/NativeImage;)V"
	)
	private void validateImageBeforeUpload(NativeImage instance, int level, int offsetX, int offsetY, int unpackSkipPixels, int unpackSkipRows, int width, int height, boolean mipmap, boolean close, Operation<Void> original) {
		if (instance.pointer == 0L) {
			throw new IllegalArgumentException(TEXT);
		}
		original.call(instance, level, offsetX, offsetY, unpackSkipPixels, unpackSkipRows, width, height, mipmap, close);
	}
	*///?}

}
