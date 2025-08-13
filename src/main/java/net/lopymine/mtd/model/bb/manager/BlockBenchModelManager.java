package net.lopymine.mtd.model.bb.manager;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.lopymine.mtd.atlas.manager.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.model.json.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.*;
import net.minecraft.util.math.Direction;
import org.slf4j.*;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.api.Response;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.other.vector.Vec3f;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.*;
import net.lopymine.mtd.doll.model.TotemDollModel;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.*;
import net.lopymine.mtd.model.bb.BBCube.*;
import net.lopymine.mtd.model.bb.BBModel.*;
import net.lopymine.mtd.utils.CodecUtils;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

public class BlockBenchModelManager {

	private static final Logger LOGGER = LoggerFactory.getLogger("%s/BlockBenchModelManager".formatted(MyTotemDoll.MOD_NAME));

	private static final Map<Identifier, Supplier<MModel>> LOADED_MODELS = new ConcurrentHashMap<>();

	private static final Set<String> SUPPORTED_MODEL_FORMATS = Set.of("java_block", "free_rotation");

	@Nullable
	public static MModel getModel(Identifier id) {
		return getModelAsResponse(id).value();
	}

	public static Response<MModel> getModelAsResponse(Identifier id) {
		Supplier<MModel> model = LOADED_MODELS.get(id);

		if (model == null) {
			BBModel blockBenchModel = parseModel(id);
			if (blockBenchModel == null) {
				LOADED_MODELS.put(id, () -> null);
				return Response.empty(-1);
			}

			Supplier<MModel> supplier = createMModelSupplerFromBBModel(blockBenchModel);
			LOADED_MODELS.put(id, supplier);

			return Response.of(0, supplier.get());
		}

		return Response.of(0, model.get());
	}

	public static void getModelAsyncAsResponse(Identifier id, Consumer<Response<MModel>> consumer) {
		Supplier<MModel> model = LOADED_MODELS.get(id);

		if (model == null) {
			CompletableFuture.runAsync(() -> {
				BBModel blockBenchModel = parseModel(id);
				if (blockBenchModel == null) {
					LOADED_MODELS.put(id, () -> null);
					consumer.accept(Response.empty(-1));
					return;
				}

				Supplier<MModel> supplier = createMModelSupplerFromBBModel(blockBenchModel);
				LOADED_MODELS.put(id, supplier);

				consumer.accept(Response.of(0, supplier.get()));
			});

			return;
		}

		MModel value = model.get();
		consumer.accept(Response.of(value == null ? -1 : 0, value));
	}

	public static void consumeModelById(Identifier id, Consumer<MModel> consumer) {
		BlockBenchModelManager.getModelAsyncAsResponse(id, (response) -> {
			if (!response.isEmpty()) {
				MModel value = response.value();
				consumer.accept(value);
			}
		});
	}

	@Nullable
	private static BBModel parseModel(Identifier id) {
		try {
			ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
			InputStream open = resourceManager.open(id);

			JsonObject jsonObject = new Gson().fromJson(new JsonReader(new InputStreamReader(open)), JsonObject.class);

			String name = CodecUtils.decode("name", Codec.STRING, jsonObject);
			BBModelMeta meta = CodecUtils.decode("meta", BBModelMeta.CODEC, jsonObject);
			BBModelResolution resolution = CodecUtils.decode("resolution", BBModelResolution.CODEC, jsonObject);
			if (meta == null || resolution == null) {
				LOGGER.warn("Failed to parse metadata or resolution for model \"{}\"! Skipping.", name);
				return null;
			}
			if (!SUPPORTED_MODEL_FORMATS.contains(meta.getModel())) {
				LOGGER.warn("Found model with unsupported model format. Name: \"{}\", Model Format: \"{}\". Skipping.", meta.getModel(), name);
				return null;
			}
			ModelTransformation display = CodecUtils.decode("display", ModelTransformation.NONE, Transformations.MODEL_TRANSFORMATION_CODEC, jsonObject);
			Boolean frontGuiLight = CodecUtils.decode("front_gui_light", false, Codec.BOOL, jsonObject);

			List<BBCube> cubes = new ArrayList<>();
			for (JsonElement jsonElement : jsonObject.get("elements").getAsJsonArray()) {
				JsonObject element = jsonElement.getAsJsonObject();
				if (!element.get("type").getAsString().equals("cube")) {
					continue;
				}
				CodecUtils.decode(BBCube.CODEC, element, cubes::add);
			}

			List<UUID> rootCubes = new ArrayList<>();
			List<BBGroup> groups = new ArrayList<>();
			for (JsonElement jsonElement : jsonObject.get("outliner").getAsJsonArray()) {
				CodecUtils.decode(Codec.either(BBGroup.CODEC, Uuids.CODEC), jsonElement, (either) -> {
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

			return new BBModel(id, name, meta, resolution, cubes, groups, frontGuiLight, display);
		} catch (NoSuchFileException | FileNotFoundException e) {
			LOGGER.warn("Failed to find bbmodel find with id \"{}\"", id.toString());
		} catch (Exception e) {
			LOGGER.warn("Failed to load bbmodel find with id \"%s\"".formatted(id.toString()), e);
		}
		return null;
	}

	private static Supplier<MModel> createMModelSupplerFromBBModel(@NotNull BBModel model) {
		MModelBuilder builder = MModelBuilder.builder(ModelState.ROOT);

		for (BBGroup group : model.getGroups()) {
			builder.addChild(group.getName(), transformGroupsAndCubes(group, model), model.getLocation());
		}

		BBModelResolution resolution = model.getResolution();

		builder.collectAllBuiltinTextures().forEach((id) -> MyTotemDollAtlasSpriteManager.registerSprite(id, false, null));
		MyTotemDollAtlasManager.stitchAndUpdate(MyTotemDollAtlasSpriteManager.getSprites(), null);

		Supplier<MModel> supplier = () -> builder
				.withTransform(ModelTransform./*? if <=1.21.4 {*/ /*pivot *//*?} else {*/ origin /*?}*/(-16.0F, -8.0F, 0.0F))
				.build(resolution.getWidth(), resolution.getHeight())
				.initAfterBuild(model);

		if (MyTotemDollClient.getConfig().isDebugLogEnabled()) {
			String modelName = model.getName();

			LOGGER.info("Successfully loaded model \"{}\" with hierarchy:", modelName);

			String line = "—";
			String nameReplacement = line.repeat(modelName.length() + 4);
			String lines = line.repeat(10);

			LOGGER.info("{}| {} |{}", lines, modelName, lines);
			MModel get = supplier.get();
			get.logSize(LOGGER);
			LOGGER.info("{}{}{}", lines, nameReplacement, lines);
			get.logHierarchy(LOGGER);
			LOGGER.info("{}{}{}", lines, nameReplacement, lines);
		}

		return supplier;
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
		Map<Direction, BBCubeFace> map = faces.map();

		for (Direction value : map.keySet()) {
			Direction direction = value == Direction.UP || value == Direction.DOWN || value == Direction.EAST || value == Direction.WEST ? value.getOpposite() : value;
			BBCubeFace face = map.get(direction);
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
}
