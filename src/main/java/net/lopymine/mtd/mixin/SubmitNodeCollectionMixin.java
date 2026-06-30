package net.lopymine.mtd.mixin;

import java.util.*;
import net.lopymine.mtd.renderer.TotemDollFeature;
import net.lopymine.mtd.utils.mixin.MyTotemDollSubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements MyTotemDollSubmitNodeCollection {

	@Shadow private boolean wasUsed;
	@Unique
	private final List<TotemDollFeature> myTotemDoll$totemDollFeatures = new ArrayList<>();

	@Override
	public void myTotemDoll$submit(TotemDollFeature totemDollFeature) {
		this.wasUsed = true;
		this.myTotemDoll$totemDollFeatures.add(totemDollFeature);
	}

	@Override
	public List<TotemDollFeature> myTotemDoll$getFeatures() {
		return this.myTotemDoll$totemDollFeatures;
	}

	@Inject(at = @At("HEAD"), method = "clear")
	private void clearTotemDollFeatures(CallbackInfo ci) {
		this.myTotemDoll$totemDollFeatures.clear();
	}
}
