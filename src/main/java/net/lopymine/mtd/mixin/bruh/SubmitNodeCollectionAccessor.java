package net.lopymine.mtd.mixin.bruh;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SubmitNodeCollection.class)
public interface SubmitNodeCollectionAccessor {

	@Accessor("translucentModels")
	TranslucentFeatureRenderPhase getTranslucentModels();

}
