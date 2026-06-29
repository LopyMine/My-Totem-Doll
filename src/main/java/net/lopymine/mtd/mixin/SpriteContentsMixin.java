package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpriteContents.class)
public class SpriteContentsMixin {

	@Unique
	private static final String myTotemDoll$TEXT = "Wait! This crash was caused by the \"my-totem-doll\" mod SPECIFICALLY to prevent a crash via drivers. This crash was made to make debugging this unexpected error easier. Someone (maybe \"my-totem-doll\") just pushed closed sprite to upload and this shouldn't happen! Please report this crash-report to \"my-totem-doll\" issue tracker: https://github.com/LopyMine/My-Totem-Doll/issues";

	@WrapOperation(
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/CommandEncoder;writeToTexture(Lcom/mojang/blaze3d/textures/GpuTexture;Lcom/mojang/blaze3d/platform/NativeImage;IIII)V"),
			method = "uploadFirstFrame"
	)
	private void validateImageBeforeUpload(CommandEncoder instance, GpuTexture destination, NativeImage source, int mipLevel, int depthOrLayer, int destX, int destY, Operation<Void> original) {
		if (source.pixels == 0L) {
			MyTotemDollClient.LOGGER.error("UPLOADED CLOSED SPRITE, SEEMS LIKE A MY TOTEM DOLL BUG, PLEASE REPORT THIS", new Throwable());
			MyTotemDollClient.LOGGER.error(myTotemDoll$TEXT);
			throw new IllegalArgumentException(myTotemDoll$TEXT);
		}
		original.call(instance, destination, source, mipLevel, depthOrLayer, destX, destY);
	}

}
