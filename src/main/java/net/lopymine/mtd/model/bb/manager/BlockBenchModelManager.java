package net.lopymine.mtd.model.bb.manager;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import net.fabricmc.loader.api.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.api.Response;
import net.lopymine.mtd.atlas.manager.*;
import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.*;
import net.lopymine.mtd.model.bb.BBCube.*;
import net.lopymine.mtd.model.bb.BBModel.*;
import net.lopymine.mtd.utils.CodecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.resources.model.cuboid.*;
import net.minecraft.client.resources.model.cuboid.*;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.*;
import org.slf4j.*;

// 0 - success
// -1 - failed
// 99 - loading
public class BlockBenchModelManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("%s/BlockBenchModelManager".formatted(MyTotemDoll.MOD_NAME));

	private static final Map<Identifier, CompletableFuture<Response<MModelFactory>>> LOADED_MODELS = new ConcurrentHashMap<>();

	private static final Set<String> SUPPORTED_MODEL_FORMATS = Set.of("java_block", "free_rotation");

	@Nullable
	public static MModel getModel(Identifier id) {
		return createMModel(getMModelFactoryAsResponse(id)).value();
	}

	public static @NotNull Response<MModelFactory> getMModelFactoryAsResponse(Identifier id) {
		CompletableFuture<Response<MModelFactory>> future = LOADED_MODELS.computeIfAbsent(id,
				(key) -> CompletableFuture.completedFuture(createMModelFactory(id))
		);

		if (!future.isDone()) {
			return Response.empty(99);
		}

		try {
			return future.getNow(Response.empty(-1));
		} catch (Exception e) {
			LOGGER.warn("Failed to load model, exception from future:", e);
		}

		return Response.empty(-1);
	}

	public static void consumeModelById(Identifier id, Consumer<MModel> consumer) {
		BlockBenchModelManager.getModelAsyncAsResponse(id, (response) -> {
			MModel value = response.value();
			if (value != null) {
				consumer.accept(value);
			}
		});
	}

	public static void getModelAsyncAsResponse(Identifier id, Consumer<Response<MModel>> consumer) {
		LOADED_MODELS.computeIfAbsent(
				id,
				(key) -> CompletableFuture.supplyAsync(() -> createMModelFactory(id))
		).thenAccept(
				(response) -> consumer.accept(createMModel(response))
		);
	}

	private static @NotNull Response<MModel> createMModel(@Nullable Response<MModelFactory> response) {
		try {
			if (response == null) {
				return Response.empty(-11);
			}
			MModelFactory factory = response.value();
			int statusCode = response.statusCode();
			if (factory == null) {
				return Response.empty(statusCode);
			}
			return Response.of(statusCode, factory.get());
		} catch (Exception e) {
			LOGGER.warn("Failed to load model, exception from future:", e);
		}
		return Response.empty(-10);
	}

	private static @NotNull Response<MModelFactory> createMModelFactory(Identifier id) {
		Response<BBModel> response = parseModel(id);
		int statusCode = response.statusCode();
		BBModel value = response.value();
		if (value == null) {
			return Response.empty(statusCode);
		}

		MModelFactory factory = createMModelFactory(value);
		return Response.of(statusCode, factory);
	}

	@NotNull
	private static Response<BBModel> parseModel(Identifier id) {
		try {
			JsonObject jsonObject = readAsJsonObject(id);

			String name = CodecUtils.decode("name", Codec.STRING, jsonObject);
			BBModelMeta meta = CodecUtils.decode("meta", BBModelMeta.CODEC, jsonObject);
			if (meta == null) {
				LOGGER.warn("Failed to parse metadata for model \"{}\"! Skipping.", name);
				return Response.empty(101);
			}
			if (!SUPPORTED_MODEL_FORMATS.contains(meta.getModel())) {
				LOGGER.warn("Found model with unsupported model format. Name: \"{}\", Model Format: \"{}\". Skipping.", meta.getModel(), name);
				return Response.empty(102);
			}

			SemanticVersion modelVersion = SemanticVersion.parse(meta.getVersion());

			if (modelVersion.compareTo((Version) SemanticVersion.parse("5.0")) >= 0) {
				return processBBModel50(id, jsonObject, name, meta);
			} else if (modelVersion.compareTo((Version) SemanticVersion.parse("4.10")) >= 0) {
				return processBBModel410(id, jsonObject, name, meta);
			}
		} catch (NoSuchFileException | FileNotFoundException e) {
			LOGGER.warn("Failed to find bbmodel find with id \"{}\"", id.toString());
		} catch (Exception e) {
			LOGGER.warn("Failed to load bbmodel find with id \"%s\"".formatted(id.toString()), e);
		}
		return Response.empty(100);
	}

	@NotNull
	private static Response<BBModel> processBBModel410(Identifier id, JsonObject jsonObject, String name, BBModelMeta meta) {
		BBModelResolution resolution = CodecUtils.decode("resolution", BBModelResolution.CODEC, jsonObject);
		if (resolution == null) {
			LOGGER.warn("Failed to parse resolution from 4.10 format for model \"{}\"! Skipping.", name);
			return Response.empty(103);
		}

		List<BBCube> cubes = parseCubes(jsonObject);
		BBModelGroupsAndRootCubes result = parseGroupsAndCubes410(jsonObject);

		return Response.of(0, createFinalBBModel(id, jsonObject, name, meta, result.rootCubes(), result.groups(), resolution, cubes));
	}

	@NotNull
	private static Response<BBModel> processBBModel50(Identifier id, JsonObject jsonObject, String name, BBModelMeta meta) {
		BBModelResolution resolution = CodecUtils.decode("resolution", BBModelResolution.CODEC, jsonObject);
		if (resolution == null) {
			LOGGER.warn("Failed to parse resolution from 5.0 format for model \"{}\"! Skipping.", name);
			return Response.empty(103);
		}

		List<BBCube> cubes = parseCubes(jsonObject);
		BBModelGroupsAndRootCubes result = parseGroupsAndCubes50(jsonObject);

		return Response.of(0, createFinalBBModel(id, jsonObject, name, meta, result.rootCubes(), result.groups(), resolution, cubes));
	}

	private static @NotNull List<BBCube> parseCubes(JsonObject jsonObject) {
		List<BBCube> cubes = new ArrayList<>();
		for (JsonElement jsonElement : jsonObject.get("elements").getAsJsonArray()) {
			JsonObject element = jsonElement.getAsJsonObject();
			if (!element.get("type").getAsString().equals("cube")) {
				continue;
			}
			CodecUtils.decode(BBCube.CODEC, element, (cube) -> {
				cube.setFaces(parseCubeFaces(element.get("faces").getAsJsonObject()));
				cubes.add(cube);
			});
		}
		return cubes;
	}

	private static BBCubeFaces parseCubeFaces(JsonObject faces) {
		BBCubeFaces cubeFaces = new BBCubeFaces(new HashMap<>());

		for (Direction direction : Direction.values()) {
			String id = direction.getName();
			JsonObject face = faces.get(id).getAsJsonObject();
			if (face.has("texture") && face.get("texture").isJsonNull()) {
				continue;
			}
			UV uv = CodecUtils.decode("uv", UV.CODEC, face);
			Integer decodedRotation = CodecUtils.decode("rotation", Codec.INT, face);
			int rotation = decodedRotation == null ? 0 : decodedRotation;
			BBCubeFace cubeFace = new BBCubeFace(uv, rotation);
			cubeFaces.getFaces().put(direction, cubeFace);
		}

		return cubeFaces;
	}

	private static @NotNull BBModelGroupsAndRootCubes parseGroupsAndCubes50(JsonObject jsonObject) {
		List<UUID> rootCubes = new ArrayList<>();
		List<BBOutliner> outliners = new ArrayList<>();
		for (JsonElement jsonElement : jsonObject.get("outliner").getAsJsonArray()) {
			CodecUtils.decode(Codec.either(BBOutliner.CODEC, UUIDUtil.CODEC), jsonElement, (either) -> {
				Optional<BBOutliner> left = either.left();
				left.ifPresent(outliners::add);
				Optional<UUID> right = either.right();
				right.ifPresent(rootCubes::add);
			});
		}

		Map<UUID, BBGroup> map = new HashMap<>();
		for (JsonElement jsonElement : jsonObject.get("groups").getAsJsonArray()) {
			CodecUtils.decode(BBGroup.CODEC, jsonElement, (group) -> map.put(group.getUuid(), group));
		}

		List<BBGroup> groups = new ArrayList<>();
		for (BBOutliner outliner : outliners) {
			BBGroup group = convertOutlinerToBBGroup(outliner, map);
			if (group.getName().equals("root")) {
				group.setName("sub-root-" + group.getUuid());
			}
			groups.add(group);
		}

		return new BBModelGroupsAndRootCubes(rootCubes, groups);
	}

	private static BBGroup convertOutlinerToBBGroup(BBOutliner outliner, Map<UUID, BBGroup> map) {
		List<Either<BBGroup, UUID>> children = new ArrayList<>();

		for (Either<BBOutliner, UUID> either : outliner.getChildren()) {
			Optional<BBOutliner> left = either.left();
			left.ifPresent(bbOutliner -> children.add(Either.left(convertOutlinerToBBGroup(bbOutliner, map))));
			Optional<UUID> right = either.right();
			right.ifPresent(uuid -> children.add(Either.right(uuid)));
		}

		BBGroup group = map.get(outliner.getUuid());
		group.setChildren(children);
		return group;
	}

	private static @NotNull BBModelGroupsAndRootCubes parseGroupsAndCubes410(JsonObject jsonObject) {
		List<UUID> rootCubes = new ArrayList<>();
		List<BBGroup> groups = new ArrayList<>();
		for (JsonElement jsonElement : jsonObject.get("outliner").getAsJsonArray()) {
			CodecUtils.decode(Codec.either(BBGroup.CODEC, UUIDUtil.CODEC), jsonElement, (either) -> {
				Optional<BBGroup> left = either.left();
				left.ifPresent((group) -> {
					if (group.getName().equals("root")) {
						group.setName("sub-root-" + group.getUuid());
					}
					groups.add(group);
				});

				Optional<UUID> right = either.right();
				right.ifPresent(rootCubes::add);
			});
		}
		return new BBModelGroupsAndRootCubes(rootCubes, groups);
	}

	private static @NotNull BBModel createFinalBBModel(Identifier id, JsonObject jsonObject, String name, BBModelMeta meta, List<UUID> rootCubes, List<BBGroup> groups, BBModelResolution resolution, List<BBCube> cubes) {
		BBGroup rootGroup = new BBGroup(
				"root",
				new Vec3f(),
				new Vec3f(),
				0,
				true,
				UUID.randomUUID(),
				rootCubes.stream()
						.map(Either::<BBGroup, UUID>right)
						.toList()
		);
		groups.add(0, rootGroup);

		ItemTransforms display = CodecUtils.decode("display", ItemTransforms.NO_TRANSFORMS, Transformations.MODEL_TRANSFORMATION_CODEC, jsonObject);
		Boolean frontGuiLight = CodecUtils.decode("front_gui_light", false, Codec.BOOL, jsonObject);
		return new BBModel(id, name, meta, resolution, cubes, groups, frontGuiLight, display);
	}

	private static JsonObject readAsJsonObject(Identifier id) throws IOException {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		InputStream open = resourceManager.open(id);
		return new Gson().fromJson(new JsonReader(new InputStreamReader(open)), JsonObject.class);
	}

	private static MModelFactory createMModelFactory(@NotNull BBModel model) {
		MModelBuilder builder = MModelBuilder.builder(ModelState.ROOT);

		for (BBGroup group : model.getGroups()) {
			builder.addChild(group.getName(), transformGroupsAndCubes(group, model), model.getLocation());
		}

		BBModelResolution resolution = model.getResolution();

		builder.collectAllBuiltinTextures().forEach((id, updateConsumer) -> {
			MyTotemDollAtlasSpriteManager.registerDynamicSprite(id, false, updateConsumer::accept);
		});

		MyTotemDollAtlasManager.stitchAndUpdate(MyTotemDollAtlasSpriteManager.getSprites(), null);

		return () -> builder
				.withTransform(PartPose.offset(-16.0F, -8.0F, 0.0F))
				.build(resolution.getWidth(), resolution.getHeight())
				.initAfterBuild(model);
	}

	private static MModelBuilder transformGroupsAndCubes(BBGroup group, BBModel model) {
		MModelBuilder builder = MModelBuilder.builder(ModelState.GROUP);

		for (Either<BBGroup, UUID> either : group.getChildren()) {
			Optional<BBGroup> left = either.left();
			Optional<UUID> right = either.right();
			if (left.isPresent()) {
				BBGroup get = left.get();
				if (!get.isVisible()) {
					continue;
				}
				builder.addChild(get.getName(), transformGroupsAndCubes(get, model), model.getLocation());
			} else if (right.isPresent()) {
				UUID uuid = right.get();
				BBCube cube = model.getCube(uuid);
				if (cube != null && cube.isVisible()) {
					builder.addChild(cube.getUuid().toString(), getChildCube(cube), model.getLocation());
				}
			}
		}

		return builder
				.withTransform(group.getTransformation());
	}

	private static MModelBuilder getChildCube(BBCube cube) {
		Vec3f from = cube.getFrom();
		Vec3f to = cube.getTo();

		MCubeBuilder cubeBuilder = MCubeBuilder.blockBenchBuilder(from.x(), from.y(), from.z(), to.x(), to.y(), to.z())
				.withDilation(cube.getInflate());

		BBCubeFaces faces = cube.getFaces();
		Map<Direction, BBCubeFace> map = faces.getFaces();

		for (Direction value : Direction.values()) {
			Direction direction = value == Direction.UP || value == Direction.DOWN || value == Direction.EAST || value == Direction.WEST ? value.getOpposite() : value;
			BBCubeFace face = map.get(direction);
			if (face == null) {
				continue;
			}

			UV uv = face.getUv();
			if (uv.isDummy()) {
				continue;
			}

			if (value == Direction.UP || value == Direction.DOWN) {
				cubeBuilder.withSide(uv.getToU(), uv.getToV(), uv.getFromU(), uv.getFromV(), value, face.getRotation());
			} else {
				cubeBuilder.withSide(uv.getFromU(), uv.getFromV(), uv.getToU(), uv.getToV(), value, face.getRotation());
			}
		}

		return MModelBuilder.builder(ModelState.CUBE)
				.addCube(cubeBuilder)
				.withTransform(cube.getTransformation());
	}

	public static void reload() {
		LOADED_MODELS.clear();
		for (TotemDollData data : TotemDollManager.getAllLoadedDolls()) {
			data.clearAllFrameModelsCompletely();
			data.setShouldRecreateStandardModel(true);
		}
		TotemDollModel.createDollModel(); // Reloading doll at resource reloading while we can
		StandardTotemDollManager.initializeStandardDollData();
	}

	private record BBModelGroupsAndRootCubes(List<UUID> rootCubes, List<BBGroup> groups) {

	}
}
