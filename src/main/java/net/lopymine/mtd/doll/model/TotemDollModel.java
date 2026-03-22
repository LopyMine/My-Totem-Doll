package net.lopymine.mtd.doll.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.atlas.*;
import net.lopymine.mtd.atlas.manager.MyTotemDollAtlasManager;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollSprites;
import net.lopymine.mtd.model.base.*;
import net.lopymine.mtd.model.bb.manager.BlockBenchModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class TotemDollModel extends Model<Object> {

	public static final Identifier NONE = MyTotemDoll.id("none");
	public static final Identifier TWO_D_MODEL_ID = MyTotemDoll.id("dolls/2d_doll.bbmodel");
	public static final Identifier THREE_D_MODEL_id = MyTotemDoll.id("dolls/3d_doll.bbmodel");

	private final MModel main;

	private final MModelCollection
			head,
			body,
			leftArmSlim,
			rightArmSlim,
			leftArmWide,
			rightArmWide,
			leftLeg,
			rightLeg;

	private final MModelCollection
			cape,
			elytra,
			ears;

	private final Map<String, MModelCollection> collections = new HashMap<>();

	private boolean slim;

	private Drawer drawer;

	public TotemDollModel(MModel root, boolean slim) {
		super(root, RenderTypes::entityTranslucent);

		this.head         = root.findModels("head");
		this.body         = root.findModels("body");
		this.leftArmSlim  = root.findModels("left_arm_slim");
		this.rightArmSlim = root.findModels("right_arm_slim");
		this.leftArmWide  = root.findModels("left_arm_wide");
		this.rightArmWide = root.findModels("right_arm_wide");
		this.leftLeg      = root.findModels("left_leg");
		this.rightLeg     = root.findModels("right_leg");

		this.cape   = root.findModels("cape");
		this.elytra = root.findModels("elytra");
		this.ears   = root.findModels("ears");

		this.initCollectionsMap();

		this.main = root;
		this.slim = slim;

		disableIfPresent(this.leftArmSlim);
		disableIfPresent(this.rightArmSlim);
		disableIfPresent(this.leftArmWide);
		disableIfPresent(this.rightArmWide);

		this.resetPartsVisibility();
	}

	public static MModel createDollModel() {
		MModel model = BlockBenchModelManager.getModel(MyTotemDollConfig.getInstance().getStandardTotemDollModelValue());
		MModel mmodel = model == null ? BlockBenchModelManager.getModel(THREE_D_MODEL_id) : model;
		if (mmodel == null) {
			throw new IllegalArgumentException("Failed to find standard doll model! [TotemDollModel.class]");
		}
		return mmodel;
	}

	public static void enableIfPresent(MModelCollection collection) {
		collection.setVisible(true);
	}

	public static void disableIfPresent(MModelCollection collection) {
		collection.setVisible(false);
	}

	public static void enableSkipRenderingIfPresent(MModelCollection collection) {
		collection.setSkipRendering(true);
	}

	public static void disableSkipRenderingIfPresent(MModelCollection collection) {
		collection.setSkipRendering(false);
	}

	private void initCollectionsMap() {
		this.addCollectionToCollectionsMap(this.head);
		this.addCollectionToCollectionsMap(this.body);
		this.addCollectionToCollectionsMap(this.leftArmSlim);
		this.addCollectionToCollectionsMap(this.rightArmSlim);
		this.addCollectionToCollectionsMap(this.leftArmWide);
		this.addCollectionToCollectionsMap(this.rightArmWide);
		this.addCollectionToCollectionsMap(this.leftLeg);
		this.addCollectionToCollectionsMap(this.rightLeg);
		this.addCollectionToCollectionsMap(this.cape);
		this.addCollectionToCollectionsMap(this.elytra);
		this.addCollectionToCollectionsMap(this.ears);
	}

	private void addCollectionToCollectionsMap(MModelCollection collection) {
		if (collection.isEmpty()) {
			return;
		}
		this.collections.put(collection.getId(), collection);
	}

	public void resetPartsVisibility() {
		enableSkipRenderingIfPresent(this.cape);
		enableIfPresent(this.cape);

		enableSkipRenderingIfPresent(this.ears);
		enableIfPresent(this.ears);

		enableSkipRenderingIfPresent(this.elytra);
		disableIfPresent(this.elytra);
	}

	public void apply(TotemDollSprites textures) {
		this.slim = textures.getArmsType().isSlim();
		this.resetPartsVisibility();
	}

	public MModelCollection getLeftArm() {
		return this.slim ? this.leftArmSlim : this.leftArmWide;
	}

	public MModelCollection getRightArm() {
		return this.slim ? this.rightArmSlim : this.rightArmWide;
	}

	public Drawer getDrawer() {
		if (this.drawer == null) {
			this.drawer = new Drawer(this);
		}
		this.drawer.prepareForRender();
		return this.drawer;
	}

	@Nullable
	public MModelCollection getCollectionOfPart(String part) {
		return this.collections.get(part);
	}

	public static class Drawer {

		private final Map<String, AtlasSprite> sprites = new HashMap<>();

		private final TotemDollModel model;

		public Drawer(TotemDollModel model) {
			this.model = model;
		}

		public void requestDrawingPartWithSprite(String part, AtlasSprite sprite) {
			this.sprites.put(part, sprite);
		}

		public void draw(PoseStack matrices, MultiBufferSource provider, AtlasSprite mainTexture, int light, int overlay, int color) {
			LockableAtlasTexture atlasTexture = MyTotemDollAtlasManager.getNullableAtlasTexture();
			if (atlasTexture == null) {
				MyTotemDollClient.LOGGER.error("Game tried to render doll model, but atlas not initialized yet!");
				return;
			}

			MModelCollection leftArm = this.model.getLeftArm();
			MModelCollection rightArm = this.model.getRightArm();

			enableIfPresent(leftArm);
			enableIfPresent(rightArm);

			RenderType renderLayer = MyTotemDollAtlasManager.getRenderLayer();

			boolean wasLocked = atlasTexture.isLocked();
			if (!wasLocked) {
				atlasTexture.setLocked(true);
			}
			this.model.getMain().draw(matrices, provider, atlasTexture.getAtlas(), renderLayer, mainTexture, this.sprites, light, overlay, color);
			if (!wasLocked) {
				atlasTexture.setLocked(false);
			}

			disableIfPresent(leftArm);
			disableIfPresent(rightArm);

			this.sprites.clear();
		}

		public void prepareForRender() {
			this.sprites.clear();
		}
	}
}