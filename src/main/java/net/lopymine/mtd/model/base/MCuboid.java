package net.lopymine.mtd.model.base;

import java.util.Set;
import lombok.Getter;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.*;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

@Getter
public class MCuboid extends ModelPart.Cube {

	private static final Set<Direction> EMPTY_SET = Set.of();
	private final CubeDeformation dilation;

	public MCuboid(Vector3f pos, Vector3f size, Polygon[] quads, CubeDeformation dilation) {
		super(0, 0, pos.x(), pos.y(), pos.z(), size.x(), size.y(), size.z(), 0, 0, 0, false, 0, 0, EMPTY_SET);
		this.polygons = quads;
		this.dilation = dilation;
	}

	public Cube asCuboid() {
		return this;
	}
}
