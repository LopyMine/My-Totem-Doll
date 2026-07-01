package net.lopymine.mtd.extension;

import com.mojang.blaze3d.vertex.PoseStack.Pose;

public class MatrixStackEntryExtension {

	public static void copyFrom(Pose entry, Pose anotherEntry) {
		entry.pose().set(anotherEntry.pose());
		entry.normal().set(anotherEntry.normal());
		entry.trustedNormals = anotherEntry.trustedNormals;
	}

}
