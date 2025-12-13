package net.lopymine.mtd.gui.widget.tag;

import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.tag.Tag;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.tooltip.IRequestableTooltipScreen;

import java.util.List;
import org.jetbrains.annotations.Nullable;

//? if >=1.21 {
import net.minecraft.client.gui.screen.ButtonTextures;
//?} else {
/*import net.lopymine.mtd.utils.ButtonTextures;
*///?}

//? if >=1.21.9 {
import net.minecraft.client.input.AbstractInput;
 //?}

@Getter
@Setter
public class TagButtonWidget extends ButtonWidget {

	public static final Identifier INACTIVE_TEXTURE = MyTotemDoll.id("textures/gui/tag_menu/button_inactive.png");

	public static final ButtonTextures TEXTURES = new ButtonTextures(
			MyTotemDoll.id("textures/gui/tag_menu/button_pressed.png"),
			MyTotemDoll.id("textures/gui/tag_menu/button_unpressed.png"),
			MyTotemDoll.id("textures/gui/tag_menu/button_pressed_hovered.png"),
			MyTotemDoll.id("textures/gui/tag_menu/button_unpressed_hovered.png")
	);

	private Tag tag;
	private String text;
	private Identifier icon;

	private boolean pressed;
	@Nullable
	private net.minecraft.text.Text tooltipText;
	private boolean canBeHovered = true;

	public TagButtonWidget(Tag tag, int x, int y, TagPressAction pressAction) {
		super(x, y, 14, 14, net.minecraft.text.Text.of(""), (widget) -> pressAction.onPress((TagButtonWidget) widget), ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
		this.tag          = tag;
		this.text         = String.valueOf(tag.getTag());
		this.icon         = TagsManager.getTagIcon(this.text.charAt(0));
	}

	@Override
	public void onPress(/*? if >=1.21.9 {*/ AbstractInput input /*?}*/) {
		this.pressed = !this.pressed;
		super.onPress(/*? if >=1.21.9 {*/ input /*?}*/);
	}

	public void setPressed(boolean pressed) {
		this.setPressed(pressed, false);
	}

	public void setPressed(boolean enabled, boolean callback) {
		this.pressed = enabled;
		if (callback) {
			this.onPress.onPress(this);
		}
	}

	public void setTooltip(@Nullable net.minecraft.text.Text text) {
		this.tooltipText = text;
	}

	//? if >=1.21.11 {
	@Override
	protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		this.renderPlease(context);
	}
	//?} else {
	/*@Override
	public void /^? if >=1.21 {^/renderWidget/^?} else {^//^renderButton^//^?}^/(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderPlease(context);
	}
	*///?}

	private void renderPlease(DrawContext context) {
		this.renderButton(context, this.getX(), this.getY());
		this.requestTooltip();
	}

	protected void renderButton(DrawContext context, int x, int y) {
		this.renderBackground(context, x, y);
		this.renderIcon(context, x, y);
	}

	protected void renderIcon(DrawContext context, int x, int y) {
		DrawUtils.drawTexture(context, this.icon, x + (this.getWidth() / 2) - 5, y + (this.getHeight() / 2) - 5, 0, 0, 10, 10, 10, 10);
	}

	protected void renderBackground(DrawContext context, int x, int y) {
		Identifier texture = !this.active ? INACTIVE_TEXTURE : TEXTURES.get(this.isPressed(), this.isHovered());
		DrawUtils.drawTexture(context, texture, x, y, 0, 0, this.width, this.height, this.width, this.height);
	}

	public void requestTooltip() {
		MinecraftClient client = MinecraftClient.getInstance();
		Screen screen = client.currentScreen;

		if (!this.isHovered()) {
			return;
		}

		TooltipComponent component = this.getTooltipComponent();
		if (component == null) {
			return;
		}

		if (!(screen instanceof IRequestableTooltipScreen tooltipScreen)) {
			return;
		}

		tooltipScreen.myTotemDoll$requestTooltip(((c, x, y, d) -> {
			DrawUtils.drawTooltip(c, List.of(component), x, y);
		}));
	}

	protected @Nullable TooltipComponent getTooltipComponent() {
		if (this.tooltipText == null) {
			return null;
		}
		return TooltipComponent.of(this.tooltipText.asOrderedText());
	}

	public boolean over(double mouseX, double mouseY) {
		return this.active
				&& this.visible
				&& mouseX >= (double)this.getX()
				&& mouseY >= (double)this.getY()
				&& mouseX < (double)(this.getX() + this.getWidth())
				&& mouseY < (double)(this.getY() + this.getHeight());
	}

	@Override
	public boolean isHovered() {
		return super.isHovered() && this.isCanBeHovered();
	}

	@FunctionalInterface
	public interface TagPressAction {

		void onPress(TagButtonWidget button);

	}
}
