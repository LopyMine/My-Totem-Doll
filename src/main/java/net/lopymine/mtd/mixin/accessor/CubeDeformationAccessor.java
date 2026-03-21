package net.lopymine.mtd.mixin.accessor;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CubeDeformation.class)
public interface CubeDeformationAccessor {

	@Accessor("growX")
	float getGrowX();

	@Accessor("growY")
	float getRadiusY();

	@Accessor("growZ")
	float getRadiusZ();
}
