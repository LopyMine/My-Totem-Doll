package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.other.vector.Vec2i;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.gui.widget.info.*;
import net.lopymine.mtd.gui.widget.tag.*;
import net.lopymine.mtd.gui.widget.tag.TagMenuWidget.Renamer;
import net.lopymine.mtd.tag.Tag;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
@ExtensionMethod(ItemStackExtension.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> implements MTDAnvilScreen {

	@Shadow
	private EditBox name;
	@Unique
	@Nullable
	private DraggingTagButtonWidget tagButtonWidget = null;
	@Unique
	@Nullable
	private TagMenuWidget tagMenuWidget = null;
	@Unique
	@Nullable
	private SmallInfoWidget infoWidget = null;
	@Unique
	@Nullable
	private TipsWidget tipsWidget = null;
	@Unique
	private boolean currentVisibleState = false;

	public AnvilScreenMixin(AnvilMenu handler, Inventory playerInventory, Component title, Identifier texture) {
		super(handler, playerInventory, title, texture);
	}

	@Shadow
	protected abstract void subInit();

	@Inject(at = @At("HEAD"), method = "subInit")
	private void setupTagMenu(CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return;
		}

		ItemStack stackOne = this.menu.getSlot(0).getItem();
		ItemStack stackTwo = this.menu.getSlot(2).getItem();
		boolean bl = MyTotemDollClient.canProcess(stackOne) && !stackOne.isEmpty();

		//

		this.tagMenuWidget         = new TagMenuWidget(0, 0, new Renamer() {
			@Override
			public String getName() {
				return AnvilScreenMixin.this.name.getValue();
			}

			@Override
			public void setName(String name) {
				AnvilScreenMixin.this.name.setValue(name);
			}
		});
		this.tagMenuWidget.visible = this.currentVisibleState;
		if (this.tagMenuWidget.visible) {
			this.tagMenuWidget.updateButtons(stackTwo.isEmpty() ? stackOne : stackTwo);
		}

		//

		this.infoWidget         = new SmallInfoWidget(0, 0);
		this.infoWidget.visible = this.tagMenuWidget.visible;

		//

		this.tipsWidget         = new TipsWidget(0, 0);
		this.tipsWidget.visible = this.tagMenuWidget.visible;

		//

		Vec2i originalPos = MyTotemDollConfig.getNewInstance().getTagButtonPos();
		this.tagButtonWidget         = new DraggingTagButtonWidget(
				Tag.simple('4'),
				this.leftPos,
				this.topPos,
				this.leftPos + originalPos.getX(),
				this.topPos + originalPos.getY(),
				0,
				0,
				(b) -> {
					this.currentVisibleState = b.isPressed();
					this.resize(this.width, this.height);
				});
		this.tagButtonWidget.visible = bl;
		this.tagButtonWidget.setPressed(this.tagMenuWidget.visible);

		//

		if (this.tagMenuWidget.visible) {
			this.imageWidth = 176 + this.tagMenuWidget.getWidth() + 5 + this.infoWidget.getWidth();
		} else {
			this.imageWidth = 176;
		}

		//

		this.addRenderableWidget(this.tagMenuWidget);
		this.addRenderableOnly(this.infoWidget);
		this.addRenderableOnly(this.tipsWidget);
		this.addRenderableWidget(this.tagButtonWidget);

		//

		this.leftPos = (this.width - this.imageWidth) / 2;
		this.updateWidgets();
	}

	@Unique
	private void updateWidgets() {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (!config.isModEnabled() || this.tagButtonWidget == null || this.tagMenuWidget == null || this.infoWidget == null || this.tipsWidget == null) {
			return;
		}

		//

		int tagMenuX = this.leftPos + 176 + 1;
		int tagMenuY = this.topPos;
		this.tagMenuWidget.setPosition(tagMenuX + 10, tagMenuY + 33);

		ItemStack stackOne = this.menu.getSlot(0).getItem();
		ItemStack stackTwo = this.menu.getSlot(2).getItem();
		ItemStack result = stackTwo.isEmpty() ? stackOne : stackTwo;
		if (result.is(Items.TOTEM_OF_UNDYING)) {
			this.tagMenuWidget.updateButtons(result);
			this.tagMenuWidget.updateCustomModelTagButtons(result);
		}

		//

		int infoWidgetX = tagMenuX + 50 + 2;
		int infoWidgetY = tagMenuY + 2;
		this.infoWidget.setPosition(infoWidgetX, infoWidgetY);
		this.tipsWidget.setPosition(infoWidgetX, infoWidgetY + this.infoWidget.getHeight() + 4);

		//

		Vec2i pos = config.getTagButtonPos();
		this.tagButtonWidget.setPosition(pos.getX() + this.leftPos, pos.getY() + this.topPos);
		this.tagButtonWidget.setOriginX(this.leftPos);
		this.tagButtonWidget.setOriginY(this.topPos);
	}

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"),
			method = "extractLabels"
	)
	private void swapBackgroundValue(GuiGraphicsExtractor instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, x1, y1, x2, y2, color);
			return;
		}
		original.call(instance, x1 - this.imageWidth + 176, y1, x2 - this.imageWidth + 176, y2, color);
	}

	@Inject(
			at = @At("TAIL"),
			method = "extractBackground"
	)
	private void updateWidgetPositions(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return;
		}
		this.updateWidgets();
		if (this.tagMenuWidget != null && this.tagMenuWidget.visible) {
			int x = this.leftPos + 176 + 1;
			int y = this.topPos;
			DrawUtils.drawTexture(graphics, TagMenuWidget.BACKGROUND, x, y, 0, 0, 50, 166, 50, 166);
			DrawUtils.drawCenteredText(graphics, MyTotemDoll.text("tag_menu.title"), x + 9, y + 9 + 6 + 3, 32);
		}
	}


	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"),
			method = "extractLabels"
	)
	private void swapBackgroundValue(GuiGraphicsExtractor instance, Font textRenderer, Component text, int x, int y, int color, Operation<Integer> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, textRenderer, text, x, y, color);
			return;
		}
		original.call(instance, textRenderer, text, x - this.imageWidth + 176, y, color);
	}

	@Inject(at = @At("HEAD"), method = "slotChanged")
	private void checkTotem(AbstractContainerMenu handler, int slotId, ItemStack stack, CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled() || this.tagButtonWidget == null || this.tagMenuWidget == null) {
			return;
		}
		if (slotId == 0) {
			this.tagButtonWidget.visible = MyTotemDollClient.canProcess(stack);
			if (!this.tagButtonWidget.visible && this.tagMenuWidget.visible) {
				this.tagButtonWidget.setPressed(false, true);
			}
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"), method = "slotChanged")
	private Component swapItemName(ItemStack stack, Operation<Component> original) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return original.call(stack);
		}
		Component customName = stack.getRealCustomName();
		if (customName == null) {
			return original.call(stack);
		}
		return customName;
	}

	@Override
	public @Nullable TagButtonWidget myTotemDoll$getTagButtonWidget() {
		return this.tagButtonWidget;
	}

	@Override
	public @Nullable TagMenuWidget myTotemDoll$getTagMenuWidget() {
		return this.tagMenuWidget;
	}
}
