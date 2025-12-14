package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.config.other.vector.Vec2i;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.gui.widget.info.*;
import net.lopymine.mtd.gui.widget.tag.*;
import net.lopymine.mtd.gui.widget.tag.TagMenuWidget.Renamer;
import net.lopymine.mtd.tag.Tag;
import net.lopymine.mtd.utils.mixin.MTDAnvilScreen;

import org.jetbrains.annotations.Nullable;

@Mixin(AnvilScreen.class)
@ExtensionMethod(ItemStackExtension.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> implements MTDAnvilScreen {

	@Shadow
	private TextFieldWidget nameField;
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

	public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
		super(handler, playerInventory, title, texture);
	}

	//? if <=1.21.10 {
	/*@Shadow
	public abstract void resize(MinecraftClient client, int width, int height);
	*///?}

	@Shadow
	protected abstract void setup();

	@Inject(at = @At("HEAD"), method = "setup")
	private void setupTagMenu(CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return;
		}

		ItemStack stackOne = this.handler.getSlot(0).getStack();
		ItemStack stackTwo = this.handler.getSlot(2).getStack();
		boolean bl = MyTotemDollClient.canProcess(stackOne) && !stackOne.isEmpty();

		//

		this.tagMenuWidget = new TagMenuWidget(0, 0, new Renamer() {
			@Override
			public String getName() {
				return AnvilScreenMixin.this.nameField.getText();
			}

			@Override
			public void setName(String name) {
				AnvilScreenMixin.this.nameField.setText(name);
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
		this.tagButtonWidget = new DraggingTagButtonWidget(
				Tag.simple('4'),
				this.x,
				this.y,
				this.x + originalPos.getX(),
				this.y + originalPos.getY(),
				0,
				0,
				(b) -> {
					this.currentVisibleState = b.isPressed();
					//? if >=1.21.11 {
					this.resize(this.width, this.height);
					//?} else {
					/*this.resize(this.client, this.width, this.height);
					*///?}
		});
		this.tagButtonWidget.visible = bl;
		this.tagButtonWidget.setPressed(this.tagMenuWidget.visible);
		
		//

		if (this.tagMenuWidget.visible) {
			this.backgroundWidth = 176 + this.tagMenuWidget.getWidth() + 5 + this.infoWidget.getWidth();
		} else {
			this.backgroundWidth = 176;
		}
		
		//

		this.addDrawableChild(this.tagMenuWidget);
		this.addDrawable(this.infoWidget);
		this.addDrawable(this.tipsWidget);
		this.addDrawableChild(this.tagButtonWidget);
		
		//

		this.x = (this.width - this.backgroundWidth) / 2;
		this.updateWidgets();
	}

	//? if =1.20.1 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setEditable(Z)V"), method = "setup")
	private void fixingMojangBugOmg(TextFieldWidget instance, boolean editable, Operation<Void> original) {
		original.call(instance, this.handler.getSlot(0).hasStack());
	}
	*///?}

	@Unique
	private void updateWidgets() {
		MyTotemDollConfig config = MyTotemDollConfig.getInstance();
		if (!config.isModEnabled() || this.tagButtonWidget == null || this.tagMenuWidget == null || this.infoWidget == null || this.tipsWidget == null) {
			return;
		}

		//

		int tagMenuX = this.x + 176 + 1;
		int tagMenuY = this.y;
		this.tagMenuWidget.setPosition(tagMenuX + 10, tagMenuY + 33);

		ItemStack stackOne = this.handler.getSlot(0).getStack();
		ItemStack stackTwo = this.handler.getSlot(2).getStack();
		ItemStack result = stackTwo.isEmpty() ? stackOne : stackTwo;
		if (result.isOf(Items.TOTEM_OF_UNDYING)) {
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
		this.tagButtonWidget.setPosition(pos.getX() + this.x, pos.getY() + this.y);
		this.tagButtonWidget.setOriginX(this.x);
		this.tagButtonWidget.setOriginY(this.y);
	}

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"),
			method = "drawForeground"
	)
	private void swapBackgroundValue(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, x1, y1, x2, y2, color);
			return;
		}
		original.call(instance, x1 - this.backgroundWidth + 176, y1, x2 - this.backgroundWidth + 176, y2, color);
	}

	@Inject(
			at = @At("TAIL"),
			method = "drawBackground"
	)
	private void updateWidgetPositions(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return;
		}
		this.updateWidgets();
		if (this.tagMenuWidget != null && this.tagMenuWidget.visible) {
			int x = this.x + 176 + 1;
			int y = this.y;
			DrawUtils.drawTexture(context, TagMenuWidget.BACKGROUND, x, y, 0, 0, 50, 166, 50, 166);
			DrawUtils.drawCenteredText(context, MyTotemDoll.text("tag_menu.title"), x + 9, y + 9 + 6 + 3, 32);
		}
	}

	//? if >=1.21.6 {
	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"),
			method = "drawForeground"
	)
	private void swapBackgroundValue(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, textRenderer, text, x, y, color);
			return;
		}
		original.call(instance, textRenderer, text, x - this.backgroundWidth + 176, y, color);
	}
	//?} else {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"), method = "drawForeground")
	private int swapBackgroundValue(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			return original.call(instance, textRenderer, text, x, y, color);
		}
		return original.call(instance, textRenderer, text, x - this.backgroundWidth + 176, y, color);
	}
	*///?}

	//? <1.21 {
	/*@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"), method = "drawInvalidRecipeArrow")
	private void swapBackgroundValue(DrawContext instance, Identifier identifier, int x, int y, int a, int b, int c, int d, Operation<Void> original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled()) {
			original.call(instance, identifier, x, y, a, b, c, d);
		}
		original.call(instance, identifier, x, y, a - this.backgroundWidth + 176, b, c, d);
	}
	*///?}

	@Inject(at = @At("HEAD"), method = "onSlotUpdate")
	private void checkTotem(ScreenHandler handler, int slotId, ItemStack stack, CallbackInfo ci) {
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

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getName()Lnet/minecraft/text/Text;"), method = "onSlotUpdate")
	private Text swapItemName(ItemStack stack, Operation<Text> original) {
		if (!MyTotemDollClient.canProcess(stack)) {
			return original.call(stack);
		}
		Text customName = stack.getRealCustomName();
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
