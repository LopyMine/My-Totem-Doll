package net.lopymine.mtd.model.base;

import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.extension.*;
import net.minecraft.client.model.geom.ModelPart.*;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

@Setter
@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
@ExtensionMethod({DilationExtension.class, ArrayExtension.class})
public class MQuadBuilder {

	private float fromU;
	private float fromV;
	private float toU;
	private float toV;
	private Direction direction;
	private int rotation;

	public static MQuadBuilder builder(float fromU, float fromV, float toU, float toV, Direction direction, int rotation) {
		return new MQuadBuilder(fromU, fromV, toU, toV, direction, rotation);
	}

	// wild stuff 0-0
	public Polygon build(int textureWidth, int textureHeight, Vector3f pos, Vector3f size, CubeDeformation dilation) {
		float f = 0.0F / textureWidth;
		float g = 0.0F / textureHeight;

		float x = pos.x();
		float y = pos.y();
		float z = pos.z();

		float fromX = x - dilation.getRadiusX();
		float fromY = y - dilation.getRadiusY();
		float fromZ = z - dilation.getRadiusZ();

		float toX = x + size.x() + dilation.getRadiusX();
		float toY = y + size.y() + dilation.getRadiusY();
		float toZ = z + size.z() + dilation.getRadiusZ();

		Vertex vertex1 = new Vertex(fromX, fromY, fromZ, 0, 0);
		Vertex vertex2 = new Vertex(toX, fromY, fromZ, 0, 0);
		Vertex vertex3 = new Vertex(toX, toY, fromZ, 0, 0);
		Vertex vertex4 = new Vertex(fromX, toY, fromZ, 0, 0);
		Vertex vertex5 = new Vertex(fromX, fromY, toZ, 0, 0);
		Vertex vertex6 = new Vertex(toX, fromY, toZ, 0, 0);
		Vertex vertex7 = new Vertex(toX, toY, toZ, 0, 0);
		Vertex vertex8 = new Vertex(fromX, toY, toZ, 0, 0);

		Vertex[] vertices = switch (this.direction) {
			case UP -> new Vertex[]{vertex3, vertex4, vertex8, vertex7};
			case DOWN -> new Vertex[]{vertex6, vertex5, vertex1, vertex2};
			case WEST -> new Vertex[]{vertex1, vertex5, vertex8, vertex4};
			case EAST -> new Vertex[]{vertex6, vertex2, vertex3, vertex7};
			case NORTH -> new Vertex[]{vertex2, vertex1, vertex4, vertex3};
			case SOUTH -> new Vertex[]{vertex5, vertex6, vertex7, vertex8};
		};

		Vertex[] rotatedVertices = this.rotation > 0 ? vertices.shift(this.rotation / 90) : vertices;

		return new Polygon(rotatedVertices, this.fromU, this.fromV, this.toU, this.toV, textureWidth, textureHeight, false, this.direction);
	}
}

