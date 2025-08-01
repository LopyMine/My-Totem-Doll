package net.lopymine.mtd.model.base;

import lombok.*;
import lombok.experimental.ExtensionMethod;
import net.minecraft.client.model.ModelTransform;


import net.lopymine.mtd.extension.*;
import net.lopymine.mtd.model.bb.ModelState;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

@SuppressWarnings("unused")
@ExtensionMethod({ModelTransformExtension.class, DilationExtension.class, IdentifierExtension.class})
public class MModelBuilder {

	private final List<MCubeBuilder> cuboidBuilders = new ArrayList<>();
	private final Map<String, MModelBuilder> childrenBuilders = new HashMap<>();
	private final ModelState state;
	@Getter
	private ModelTransform transform = ModelTransform.NONE;
	@Setter(AccessLevel.PRIVATE)
	@Getter(AccessLevel.PRIVATE)
	@Nullable
	private MModelBuilder parent;
	@Nullable
	private String name;
	@Nullable
	private Identifier builtinTexture;
	private float xScale = 1.0F;
	private float yScale = 1.0F;
	private float zScale = 1.0F;

	private MModelBuilder(ModelState state) {
		this.state = state;
	}

	public static MModelBuilder builder(ModelState state) {
		return new MModelBuilder(state);
	}

	public MModelBuilder addCube(MCubeBuilder builder) {
		this.cuboidBuilders.add(builder);
		return this;
	}

	public MModelBuilder addChild(String name, MModelBuilder builder, Identifier location) {
		builder.setParent(this);
		builder.setName(name, location);
		this.childrenBuilders.put(name, builder);
		return this;
	}

	public MModelBuilder withTransform(ModelTransform transform) {
		this.transform = ModelTransform.of(transform.getPivotX(), transform.getPivotY(), transform.getPivotZ(), transform.getPitch(), transform.getYaw(), transform.getRoll());
		return this;
	}

	public MModelBuilder withScale(float xScale, float yScale, float zScale) {
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;
		return this;
	}

	public MModel build(int textureWidth, int textureHeight) {
		return this.build(textureWidth, textureHeight, false, true);
	}

	private MModel build(int textureWidth, int textureHeight, boolean isParentRoot, boolean isRoot) {
		ModelTransform cuboidTransform = this.transform.getBlockBenchedModelTransform();

		Map<String, MModel> children = this.childrenBuilders.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> e.getValue().build(textureWidth, textureHeight, isRoot, false)));
		List<MCuboid> cuboids = this.cuboidBuilders.stream().map(builder -> builder.build(textureWidth, textureHeight, cuboidTransform)).toList();

		String name = this.getName();

		MModel part = new MModel(cuboids, children, this.state, name, this.builtinTexture);

		ModelTransform transform = this.parent == null || isParentRoot ? this.transform : this.transform.subtract(this.parent.getTransform());

		ModelTransform blockBenchedModelTransform = transform.getBlockBenchedModelTransform();

		part.setTransform(blockBenchedModelTransform);
		part.setDefaultTransform(blockBenchedModelTransform);

		part.xScale = this.xScale;
		part.yScale = this.yScale;
		part.zScale = this.zScale;

		return part;
	}

	public void setName(@Nullable String name, Identifier location) {
		this.name = name;
		if (name != null && name.endsWith(".png")) {
			if (name.contains(":")) {
				String[] split = name.split(":");
				boolean namespaceValid = Identifier.isNamespaceValid(split[0]);
				boolean pathValid = Identifier.isPathValid(split[1]);
				if (namespaceValid && pathValid) {
					this.builtinTexture = Identifier.of(name);
				}
			} else {
				this.builtinTexture = location.getFolderId().withSuffixedPath(this.getName());
			}
		}
	}

	@NotNull
	private String getName() {
		return this.name == null ? UUID.randomUUID().toString() : this.name;
	}

	public Collection<Identifier> collectAllBuiltinTextures() {
		List<Identifier> textures = new LinkedList<>();
		if (this.builtinTexture != null) {
			textures.add(this.builtinTexture);
		}
		this.childrenBuilders.values().forEach((builder) -> textures.addAll(builder.collectAllBuiltinTextures()));
		return textures;
	}
}
