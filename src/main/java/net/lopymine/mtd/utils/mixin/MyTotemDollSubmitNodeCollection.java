package net.lopymine.mtd.utils.mixin;

import java.util.List;
import net.lopymine.mtd.renderer.TotemDollFeature;

public interface MyTotemDollSubmitNodeCollection {

	void myTotemDoll$submit(TotemDollFeature totemDollFeature);

	List<TotemDollFeature> myTotemDoll$getFeatures();
}
