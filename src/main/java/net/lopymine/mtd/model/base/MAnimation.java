package net.lopymine.mtd.model.base;

import java.lang.Math;
import java.util.Map.Entry;
import java.util.function.*;
import lombok.*;
import net.lopymine.mtd.doll.renderer.DollRenderContext;
import net.lopymine.mtd.model.bb.BBAnimation;
import net.lopymine.mtd.model.bb.BBAnimation.*;
import net.minecraft.client.model.geom.*;
import org.joml.*;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
public class MAnimation {

	public static final BiConsumer<Vector3f, ModelPart> POSITION = (vec, modelPart) -> {
		modelPart.x = modelPart.getInitialPose().x() + vec.x;
		modelPart.y = modelPart.getInitialPose().y() + vec.y * - 1;
		modelPart.z = modelPart.getInitialPose().z() + vec.z;
	};

	public static final BiConsumer<Vector3f, ModelPart> ROTATION = (vec, modelPart) -> {
		modelPart.xRot = modelPart.getInitialPose().xRot() + (float) Math.toRadians(vec.x);
		modelPart.yRot = modelPart.getInitialPose().yRot() + (float) Math.toRadians(vec.y);
		modelPart.zRot = modelPart.getInitialPose().zRot() + (float) Math.toRadians(vec.z);
	};

	public static final BiConsumer<Vector3f, ModelPart> SCALE = (vec, modelPart) -> {
		modelPart.xScale = vec.x;
		modelPart.yScale = vec.y;
		modelPart.zScale = vec.z;
	};

	public static MAnimation bake(MModel model, BBAnimation animation) {


		for (Entry<String, BBAnimationEntry> entry : animation.getAnimations().entrySet()) {
			String key = entry.getKey();
			BBAnimationEntry value = entry.getValue();


		}

	}

	public MAnimation(BBAnimation animation) {
		this.animation = animation;
	}

	private BBAnimation animation;
	@Nullable
	private BBAnimationEntry currentAnimationEntry;

	private int ticks;

	public void apply(MModel model, DollRenderContext renderContext) {
		//this.currentAnimationEntry = this.animation.getAnimations().get(renderContext.name().toUpperCase(Locale.ROOT).substring(2));
		this.currentAnimationEntry = this.animation.getAnimations().get("TEST_ANIMATION3");
		Function<String, @Nullable ModelPart> partLookup = model.createPartLookup();
		if (this.currentAnimationEntry == null) {
			return;
		}

		for (Entry<String, BBAnimationBone> entry : this.currentAnimationEntry.getBones().entrySet()) {
			ModelPart modelPart = partLookup.apply(entry.getKey());
			if (modelPart == null) {
				continue;
			}
			BBAnimationBone animationBone = entry.getValue();
			this.applyBoneTransformation(animationBone.getPosition(), modelPart, POSITION);
			this.applyBoneTransformation(animationBone.getRotation(), modelPart, ROTATION);
			this.applyBoneTransformation(animationBone.getScale(), modelPart, SCALE);
		}
	}

	private void applyBoneTransformation(BBKeyframes keyframes, ModelPart modelPart, BiConsumer<Vector3f, ModelPart> consumer) {
		Entry<Integer, BBKeyframeEntry> current = keyframes.getCompiledMap().floorEntry(this.ticks);
		Entry<Integer, BBKeyframeEntry> next = keyframes.getCompiledMap().higherEntry(this.ticks);

		if (current == null) {
			return;
		}
		if (next == null) {
			consumer.accept(current.getValue().getVector(), modelPart);
			return;
		}

		float progress = (float) (this.ticks - current.getKey()) / (next.getKey() - current.getKey());

		Vector3f currentVector = current.getValue().getVector();
		Vector3f nextVector = next.getValue().getVector();

		Vector3f transformation = new Vector3f(currentVector).lerp(nextVector, progress);
		consumer.accept(transformation, modelPart);
	}

}
