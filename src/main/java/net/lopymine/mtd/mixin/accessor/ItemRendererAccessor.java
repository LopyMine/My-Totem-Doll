package net.lopymine.mtd.mixin.accessor;

import net.minecraft.client.render.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {

	@Accessor("builtinModelItemRenderer")
	BuiltinModelItemRenderer getBuiltinModelItemRenderer();
}
