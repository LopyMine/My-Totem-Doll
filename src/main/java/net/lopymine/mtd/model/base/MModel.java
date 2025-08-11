package net.lopymine.mtd.model.base;

import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.model.bb.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;

@Getter
@Setter
@ExtensionMethod({ModelTransformExtension.class, DilationExtension.class, IdentifierExtension.class})
public class MModel extends ModelPart {

	@Setter(AccessLevel.PRIVATE)
	private ModelTransformation transformation = ModelTransformation.NONE;
	private final Map<String, MModel> mChildren;
	private final List<MModel> mChildrenModels;
	private final List<MCuboid> mCuboids;
	private final ModelState state;
	private final String name;

	private boolean skipRendering = false;

	@Nullable
	private MModel parent;
	@Nullable
	private Identifier location;
	@Nullable
	private AtlasSprite builtinTexture;

	public MModel(List<MCuboid> mCuboids, Map<String, MModel> mChildren, ModelState state, String name, @Nullable AtlasSprite builtinTexture) {
		super(mCuboids.stream().map(MCuboid::asCuboid).toList(), mChildren.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().asModelPart())));
		this.state     = state;
		this.name      = name;
		this.mChildren = mChildren;
		this.mChildrenModels = new ArrayList<>(mChildren.values());
		this.mChildrenModels.forEach((mmodel) -> mmodel.setParent(this));
		this.mCuboids  = mCuboids;
		this.builtinTexture = builtinTexture;
	}

	public MModel initAfterBuild(BBModel model) {
		this.setLocation(model.getLocation());
		this.setTransformation(model.getTransformation());
		return this;
	}

	public void setLocation(@NotNull Identifier location) {
		this.location = location;
		this.mChildren.forEach((modelName, model) -> model.setLocation(location));
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, /*? if >=1.21 {*/int color/*?} else {*//*float red, float green, float blue, float alpha *//*?}*/) {
		// NO-OP
	}

	public MModelCollection findModels(String suffix) {
		ArrayList<MModel> list = new ArrayList<>();

		for (Entry<String, MModel> entry : this.mChildren.entrySet()) {
			String key = entry.getKey();
			MModel model = entry.getValue();
			if (!key.endsWith(suffix) || model == null || model.getState() != ModelState.GROUP) {
				continue;
			}
			list.add(model);
		}

		for (MModel value : this.mChildrenModels) {
			list.addAll(value.findModels(suffix).getModels());
		}

		return new MModelCollection(list, suffix);
	}

	public ModelPart asModelPart() {
		return this;
	}

	public void draw(MatrixStack matrices, VertexConsumerProvider provider, SpriteAtlasTexture atlas, RenderLayer atlasRenderLayer, AtlasSprite mainSprite, Map<String, AtlasSprite> requestedParts, int light, int overlay, /*? if >=1.21 {*/int color/*?} else {*//*float red, float green, float blue, float alpha *//*?}*/) {
		AtlasSprite providedSprite = requestedParts.get(this.getName());

		if ((this.skipRendering && providedSprite == null) || (!this.visible) || (this.mCuboids.isEmpty() && this.mChildren.isEmpty())) {
			return;
		}

		AtlasSprite currentSpriteId = this.builtinTexture == null ? providedSprite == null ? mainSprite : providedSprite : this.builtinTexture;
		if (currentSpriteId == null) {
			return;
		}

		matrices.push();
		this./*? if <=1.21.4 {*//*rotate*//*?} else {*/ applyTransform /*?}*/(matrices);
		if (!this.hidden && !this.mCuboids.isEmpty()) {
			Sprite currentSprite = atlas.getSprite(currentSpriteId.getSpriteId());
			VertexConsumer consumer = currentSprite.getTextureSpecificVertexConsumer(provider.getBuffer(atlasRenderLayer));
			this.renderCuboids(matrices.peek(), consumer, light, overlay, /*? if >=1.21 {*/ color /*?} else {*/ /*red, green, blue, alpha *//*?}*/);
		}

		for (MModel model : this.mChildrenModels) {
			model.draw(matrices, provider, atlas, atlasRenderLayer, currentSpriteId, requestedParts, light, overlay, /*? if >=1.21 {*/ color /*?} else {*/ /*red, green, blue, alpha *//*?}*/);
		}

		matrices.pop();
	}

	private int getCountOfParents() {
		return this.parent == null ? 0 : this.parent.getCountOfParents() + 1;
	}

	private String getHierarchyLine(int countOfParents) {
		if (countOfParents == 0) {
			return "";
		}
		String tab = "   ";
		String root = "└──";
		return tab.repeat(countOfParents - 1) + root;
	}

	public void logHierarchy(Logger logger) {
		int countOfParents = this.getCountOfParents();
		String hierarchyLine = this.getHierarchyLine(countOfParents);
		String dataHierarchyLine = this.getHierarchyLine(countOfParents + 1);

		String main = "%s %s".formatted(hierarchyLine, this.toString());
		String transform = "%s Transform: [%s]".formatted(dataHierarchyLine, this.getTransform().asString());
		String scale = "%s Scale: [%s %s %s]".formatted(dataHierarchyLine, this.xScale, this.yScale, this.zScale);

		logger.info(main);
		logger.info(transform);
		logger.info(scale);

		String cuboidHierarchyLine = this.getHierarchyLine(countOfParents + 1);
		String cuboidDataHierarchyLine = this.getHierarchyLine(countOfParents + 1 + 1);

		for (MCuboid value : this.mCuboids) {
			Dilation dilation = value.getDilation();

			String cuboidMain = "%s %s".formatted(cuboidHierarchyLine, this.toString());
			String cuboidFrom = "%s From: [%s %s %s]".formatted(cuboidDataHierarchyLine, value.minX, value.minY, value.minZ);
			String cuboidTo = "%s To: [%s %s %s]".formatted(cuboidDataHierarchyLine, value.maxX, value.maxY, value.maxZ);
			String cuboidDilation = "%s Dilation: [%s %s %s]".formatted(cuboidDataHierarchyLine, dilation.getRadiusX(), dilation.getRadiusY(), dilation.getRadiusZ());

			logger.info(cuboidMain);
			logger.info(cuboidFrom);
			logger.info(cuboidTo);
			logger.info(cuboidDilation);
		}

		for (MModel value : this.mChildrenModels) {
			value.logHierarchy(logger);
		}
	}

	public void logSize(Logger logger) {
		Box box = this.getBox();
		logger.info("Union Model Size:");
		logger.info("Size: [{}, {}, {}]", Math.abs(box.minX - box.maxX), Math.abs(box.minY - box.maxY), Math.abs(box.minZ - box.maxZ));
		logger.info("From: [{}, {}, {}]", box.minX, box.minY, box.minZ);
		logger.info("To:   [{}, {}, {}]", box.maxX, box.maxY, box.maxZ);
	}

	public List<MModel> getHierarchyList() {
		List<MModel> hierarchy = new ArrayList<>();
		hierarchy.add(this);

		if (this.parent != null) {
			hierarchy.addAll(this.parent.getHierarchyList());
		}

		return hierarchy;
	}

	public Box getBox() {
		float minX = 0F;
		float minY = 0F;
		float minZ = 0F;
		float maxX = 0F;
		float maxY = 0F;
		float maxZ = 0F;

		for (Cuboid cuboid : this.mCuboids) {
			minX = Math.min(minX, cuboid.minX);
			minY = Math.min(minY, cuboid.minY);
			minZ = Math.min(minZ, cuboid.minZ);

			maxX = Math.max(maxX, cuboid.maxX);
			maxY = Math.max(maxY, cuboid.maxY);
			maxZ = Math.max(maxZ, cuboid.maxZ);
		}

		Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);

		for (MModel value : this.mChildren.values()) {
			Box size = value.getBox();
			box = box.union(size);
		}

		return box;
	}

	@Override
	public String toString() {
		return "%s [%s]".formatted(this.getName(), this.getState().name().toUpperCase());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MModel that)) {
			return false;
		}
		if (that == this) {
			return true;
		}
		return Objects.equals(this.getLocation(), that.getLocation());
	}

	@Override
	public int hashCode() {
		return this.getLocation() == null ? super.hashCode() : this.getLocation().hashCode();
	}
}
