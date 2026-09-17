package net.lopymine.mtd.mixin.bruh;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.phase.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if >=26.3 {
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
//?}

@Mixin(SubmitNodeCollection.class)
public interface SubmitNodeCollectionAccessor {

	@Accessor("translucentModels")
	/*? if >=26.3 {*/FeatureRenderPhase<? super TranslucentSubmit>/*?} else {*//*TranslucentFeatureRenderPhase*//*?}*/ getTranslucentModels();

}
