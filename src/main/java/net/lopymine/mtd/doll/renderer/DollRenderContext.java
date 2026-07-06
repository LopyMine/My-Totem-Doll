package net.lopymine.mtd.doll.renderer;

import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.renderer.block.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;

import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.extension.ModelTransformationExtension;
import net.lopymine.mtd.model.base.MModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;

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

	D_FLOATING("floating"),
	D_PREVIEW("preview"),
	D_TOOLTIP("tooltip"),
	D_CUSTOM("custom");

	private final String id;

	DollRenderContext(String id) {
		this.id = id;
	}

	public static DollRenderContext of(Object object) {
		if (object instanceof
				net.minecraft.world.item.ItemDisplayContext
						mode) {
			return switch (mode) {
				case THIRD_PERSON_LEFT_HAND -> D_THIRD_PERSON_LEFT_HAND;
				case THIRD_PERSON_RIGHT_HAND -> D_THIRD_PERSON_RIGHT_HAND;
				case FIRST_PERSON_LEFT_HAND -> D_FIRST_PERSON_LEFT_HAND;
				case FIRST_PERSON_RIGHT_HAND -> D_FIRST_PERSON_RIGHT_HAND;
				case HEAD -> D_HEAD;
				case GUI -> D_GUI;
				case GROUND -> D_GROUND;
				case FIXED -> D_FIXED;
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
			default -> ItemTransform.NO_TRANSFORM;
		};
	}

	public void apply(MModel model, PoseStack matrices) {
		ItemTransform transformation = get(model.getTransformation());
		transformation.apply(this.isLeftHanded(),  matrices);
	}

	public boolean isLeftHanded() {
		return this == D_FIRST_PERSON_LEFT_HAND || this == D_THIRD_PERSON_LEFT_HAND;
	}
}
