package net.lopymine.mtd.doll.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ModelTransformationExtension;
import net.lopymine.mtd.model.base.MModel;
import net.minecraft.client.resources.model.cuboid.*;

@Getter
@ExtensionMethod(ModelTransformationExtension.class)
public enum DollRenderContext {

	D_NONE("none"),
	D_THIRD_PERSON_LEFT_HAND("thirdperson_lefthand"),
	D_THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
	D_FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
	D_FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
	D_HEAD("head"),
	D_GUI("gui"),
	D_GROUND("ground"),
	D_FIXED("fixed"),
	D_ON_SHELF("on_shelf"),

	D_FLOATING("floating"),
	D_PREVIEW("preview"),
	D_TOOLTIP("tooltip"),
	D_CUSTOM("custom");

	private final String id;

	DollRenderContext(String id) {
		this.id = id;
	}

	public static DollRenderContext of(Object object) {
		if (object instanceof net.minecraft.world.item.ItemDisplayContext context) {
			return switch (context) {
				case THIRD_PERSON_LEFT_HAND -> D_THIRD_PERSON_LEFT_HAND;
				case THIRD_PERSON_RIGHT_HAND -> D_THIRD_PERSON_RIGHT_HAND;
				case FIRST_PERSON_LEFT_HAND -> D_FIRST_PERSON_LEFT_HAND;
				case FIRST_PERSON_RIGHT_HAND -> D_FIRST_PERSON_RIGHT_HAND;
				case HEAD -> D_HEAD;
				case GUI -> D_GUI;
				case GROUND -> D_GROUND;
				case FIXED -> D_FIXED;
				case ON_SHELF -> D_ON_SHELF;
				default -> D_NONE;
			};
		}
		MyTotemDollClient.LOGGER.error("Failed to get DollRenderContext from object: {}", object.getClass().getName());
		return D_NONE;
	}

	public ItemTransform get(ItemTransforms transformation) {
		return switch (this) {
			case D_THIRD_PERSON_LEFT_HAND -> transformation.getTl();
			case D_THIRD_PERSON_RIGHT_HAND -> transformation.getTr();
			case D_FIRST_PERSON_LEFT_HAND -> transformation.getFl();
			case D_FIRST_PERSON_RIGHT_HAND -> transformation.getFr();
			case D_HEAD -> transformation.getHead();
			case D_GUI -> transformation.getGui();
			case D_GROUND -> transformation.getGround();
			case D_FIXED -> transformation.getFixed();
			case D_ON_SHELF -> transformation.getOnShelf();
			default -> ItemTransform.NO_TRANSFORM;
		};
	}

	public void apply(MModel model, PoseStack matrices) {
		ItemTransform transformation = get(model.getTransformation());
		Pose peek = matrices.last();
		transformation.apply(this.isLeftHanded(), peek);
		peek.translate(0.5F, 0.5F, 0.5F);
	}

	public boolean isLeftHanded() {
		return this == D_FIRST_PERSON_LEFT_HAND || this == D_THIRD_PERSON_LEFT_HAND;
	}
}
