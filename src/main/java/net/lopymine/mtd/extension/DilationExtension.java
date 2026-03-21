package net.lopymine.mtd.extension;

import net.lopymine.mtd.mixin.accessor.CubeDeformationAccessor;
import net.minecraft.client.model.geom.builders.CubeDeformation;

public class DilationExtension {

	public static float getRadiusX(CubeDeformation dilation) {
		return ((CubeDeformationAccessor) dilation).getGrowX();
	}

	public static float getRadiusY(CubeDeformation dilation) {
		return ((CubeDeformationAccessor) dilation).getRadiusY();
	}

	public static float getRadiusZ(CubeDeformation dilation) {
		return ((CubeDeformationAccessor) dilation).getRadiusZ();
	}
}
