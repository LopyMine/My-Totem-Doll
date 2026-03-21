package net.lopymine.mtd.model.base;

import com.mojang.blaze3d.vertex.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.atlas.AtlasSprite;
import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.model.bb.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;

@Getter
@Setter
@ExtensionMethod({ModelTransformExtension.class, DilationExtension.class, IdentifierExtension.class})
public class MModel extends ModelPart {

	private final Map<String, MModel> mChildren;
	private final List<MModel> mChildrenModels;
	private final List<MCuboid> mCuboids;
	private final ModelState state;
	private final String name;
	@Setter(AccessLevel.PRIVATE)
	private ItemTransforms transformation = ItemTransforms.NO_TRANSFORMS;
	private boolean skipRendering = false;

	@Nullable
	private MModel parent;
	@Nullable
	private Identifier location;
	@Nullable
	private AtlasSprite builtinTexture;

	public MModel(List<MCuboid> mCuboids, Map<String, MModel> mChildren, ModelState state, String name, @Nullable AtlasSprite builtinTexture) {
		super(mCuboids.stream().map(MCuboid::asCuboid).toList(), mChildren.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().asModelPart())));
		this.state           = state;
		this.name            = name;
		this.mChildren       = mChildren;
		this.mChildrenModels = new ArrayList<>(mChildren.values());
		this.mChildrenModels.forEach((mmodel) -> mmodel.setParent(this));
		this.mCuboids       = mCuboids;
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
	public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
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

	public void draw(PoseStack matrices, MultiBufferSource provider, TextureAtlas atlas, RenderType atlasRenderLayer, AtlasSprite mainSprite, Map<String, AtlasSprite> requestedParts, int light, int overlay, int color) {
		AtlasSprite providedSprite = requestedParts.get(this.getName());

		if ((this.skipRendering && providedSprite == null) || (!this.visible) || (this.mCuboids.isEmpty() && this.mChildren.isEmpty())) {
			return;
		}

		AtlasSprite currentSpriteId = this.builtinTexture == null ? providedSprite == null ? mainSprite : providedSprite : this.builtinTexture;
		if (currentSpriteId == null) {
			return;
		}

		matrices.pushPose();
		this.translateAndRotate(matrices);
		if (!this.skipDraw && !this.mCuboids.isEmpty()) {
			TextureAtlasSprite currentSprite = atlas.getSprite(currentSpriteId.getSpriteId());
			VertexConsumer consumer = currentSprite.wrap(provider.getBuffer(atlasRenderLayer));
			this.compile(matrices.last(), consumer, light, overlay, color);
		}

		for (MModel model : this.mChildrenModels) {
			model.draw(matrices, provider, atlas, atlasRenderLayer, currentSpriteId, requestedParts, light, overlay, color);
		}

		matrices.popPose();
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
		String transform = "%s Transform: [%s]".formatted(dataHierarchyLine, this.storePose().asString());
		String scale = "%s Scale: [%s %s %s]".formatted(dataHierarchyLine, this.xScale, this.yScale, this.zScale);

		logger.info(main);
		logger.info(transform);
		logger.info(scale);

		String cuboidHierarchyLine = this.getHierarchyLine(countOfParents + 1);
		String cuboidDataHierarchyLine = this.getHierarchyLine(countOfParents + 1 + 1);

		for (MCuboid value : this.mCuboids) {
			CubeDeformation dilation = value.getDilation();

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
		AABB box = this.getBox();
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

	public AABB getBox() {
		float minX = 0F;
		float minY = 0F;
		float minZ = 0F;
		float maxX = 0F;
		float maxY = 0F;
		float maxZ = 0F;

		for (Cube cuboid : this.mCuboids) {
			minX = Math.min(minX, cuboid.minX);
			minY = Math.min(minY, cuboid.minY);
			minZ = Math.min(minZ, cuboid.minZ);

			maxX = Math.max(maxX, cuboid.maxX);
			maxY = Math.max(maxY, cuboid.maxY);
			maxZ = Math.max(maxZ, cuboid.maxZ);
		}

		AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

		for (MModel value : this.mChildren.values()) {
			AABB size = value.getBox();
			box = box.minmax(size);
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
