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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
	private DraggingTagButtonWidget myTotemDoll$tagButtonWidget = null;
	@Unique
	@Nullable
	private TagMenuWidget myTotemDoll$tagMenuWidget = null;
	@Unique
	@Nullable
	private SmallInfoWidget myTotemDoll$infoWidget = null;
	@Unique
	@Nullable
	private TipsWidget myTotemDoll$tipsWidget = null;
	@Unique
	private boolean myTotemDoll$currentVisibleState = false;

	public AnvilScreenMixin(AnvilMenu handler, Inventory playerInventory, Component title, ResourceLocation texture) {
		super(handler, playerInventory, title, texture);
	}

	@Shadow
	public abstract void resize(Minecraft client, int width, int height);

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

		this.myTotemDoll$tagMenuWidget         = new TagMenuWidget(0, 0, new Renamer() {
			@Override
			public String getName() {
				return AnvilScreenMixin.this.name.getValue();
			}

			@Override
			public void setName(String name) {
				AnvilScreenMixin.this.name.setValue(name);
			}
		});
		this.myTotemDoll$tagMenuWidget.visible = this.myTotemDoll$currentVisibleState;
		if (this.myTotemDoll$tagMenuWidget.visible) {
			this.myTotemDoll$tagMenuWidget.updateButtons(stackTwo.isEmpty() ? stackOne : stackTwo);
		}

		//

		this.myTotemDoll$infoWidget         = new SmallInfoWidget(0, 0);
		this.myTotemDoll$infoWidget.visible = this.myTotemDoll$tagMenuWidget.visible;

		//

		this.myTotemDoll$tipsWidget         = new TipsWidget(0, 0);
		this.myTotemDoll$tipsWidget.visible = this.myTotemDoll$tagMenuWidget.visible;

		//

		Vec2i originalPos = MyTotemDollConfig.getNewInstance().getTagButtonPos();
		this.myTotemDoll$tagButtonWidget         = new DraggingTagButtonWidget(
				Tag.simple('4'),
				this.leftPos,
				this.topPos,
				this.leftPos + originalPos.getX(),
				this.topPos + originalPos.getY(),
				0,
				0,
				(b) -> {
					this.myTotemDoll$currentVisibleState = b.isPressed();
					this.resize(this.minecraft, this.width, this.height);
				});
		this.myTotemDoll$tagButtonWidget.visible = bl;
		this.myTotemDoll$tagButtonWidget.setPressed(this.myTotemDoll$tagMenuWidget.visible);

		//

		if (this.myTotemDoll$tagMenuWidget.visible) {
			this.imageWidth = 176 + this.myTotemDoll$tagMenuWidget.getWidth() + 5 + this.myTotemDoll$infoWidget.getWidth();
		} else {
			this.imageWidth = 176;
		}

		//

		this.addRenderableWidget(this.myTotemDoll$tagMenuWidget);
		this.addRenderableOnly(this.myTotemDoll$infoWidget);
		this.addRenderableOnly(this.myTotemDoll$tipsWidget);
		this.addRenderableWidget(this.myTotemDoll$tagButtonWidget);

		//

		this.leftPos = (this.width - this.imageWidth) / 2;
		this.myTotemDoll$updateWidgets();
	}


	@Unique
	private void myTotemDoll$updateWidgets() {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (!config.isModEnabled() || this.myTotemDoll$tagButtonWidget == null || this.myTotemDoll$tagMenuWidget == null || this.myTotemDoll$infoWidget == null || this.myTotemDoll$tipsWidget == null) {
			return;
		}

		//

		int tagMenuX = this.leftPos + 176 + 1;
		int tagMenuY = this.topPos;
		this.myTotemDoll$tagMenuWidget.setPosition(tagMenuX + 10, tagMenuY + 33);

		ItemStack stackOne = this.menu.getSlot(0).getItem();
		ItemStack stackTwo = this.menu.getSlot(2).getItem();
		ItemStack result = stackTwo.isEmpty() ? stackOne : stackTwo;
		if (result.is(Items.TOTEM_OF_UNDYING)) {
			this.myTotemDoll$tagMenuWidget.updateButtons(result);
			this.myTotemDoll$tagMenuWidget.updateCustomModelTagButtons(result);
		}

		//

		int infoWidgetX = tagMenuX + 50 + 2;
		int infoWidgetY = tagMenuY + 2;
		this.myTotemDoll$infoWidget.setPosition(infoWidgetX, infoWidgetY);
		this.myTotemDoll$tipsWidget.setPosition(infoWidgetX, infoWidgetY + this.myTotemDoll$infoWidget.getHeight() + 4);

		//

		Vec2i pos = config.getTagButtonPos();
		this.myTotemDoll$tagButtonWidget.setPosition(pos.getX() + this.leftPos, pos.getY() + this.topPos);
		this.myTotemDoll$tagButtonWidget.setOriginX(this.leftPos);
		this.myTotemDoll$tagButtonWidget.setOriginY(this.topPos);
	}

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"),
			method = "renderLabels"
	)
	private void swapBackgroundValue(GuiGraphics instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, x1, y1, x2, y2, color);
			return;
		}
		original.call(instance, x1 - this.imageWidth + 176, y1, x2 - this.imageWidth + 176, y2, color);
	}

	@Inject(
			at = @At("TAIL"),
			method = "renderBg"
	)
	private void updateWidgetPositions(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return;
		}
		this.myTotemDoll$updateWidgets();
		if (this.myTotemDoll$tagMenuWidget != null && this.myTotemDoll$tagMenuWidget.visible) {
			int x = this.leftPos + 176 + 1;
			int y = this.topPos;
			DrawUtils.drawTexture(context, TagMenuWidget.BACKGROUND, x, y, 0, 0, 50, 166, 50, 166);
			DrawUtils.drawCenteredText(context, MyTotemDoll.text("tag_menu.title"), x + 9, y + 9 + 6 + 3, 32);
		}
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"), method = "renderLabels")
	private int swapBackgroundValue(GuiGraphics instance, Font textRenderer, Component text, int x, int y, int color, Operation<Integer> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return original.call(instance, textRenderer, text, x, y, color);
		}
		return original.call(instance, textRenderer, text, x - this.imageWidth + 176, y, color);
	}


	@Inject(at = @At("HEAD"), method = "slotChanged")
	private void checkTotem(AbstractContainerMenu handler, int slotId, ItemStack stack, CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled() || this.myTotemDoll$tagButtonWidget == null || this.myTotemDoll$tagMenuWidget == null) {
			return;
		}
		if (slotId == 0) {
			this.myTotemDoll$tagButtonWidget.visible = MyTotemDollClient.canProcess(stack);
			if (!this.myTotemDoll$tagButtonWidget.visible && this.myTotemDoll$tagMenuWidget.visible) {
				this.myTotemDoll$tagButtonWidget.setPressed(false, true);
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
		return this.myTotemDoll$tagButtonWidget;
	}

	@Override
	public @Nullable TagMenuWidget myTotemDoll$getTagMenuWidget() {
		return this.myTotemDoll$tagMenuWidget;
	}
}
