package net.lopymine.mtd.gui.widget.tag;

import java.util.List;
import lombok.*;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.tag.Tag;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.DrawUtils;
import net.lopymine.mtd.utils.tooltip.IRequestableTooltipScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;


@Getter
@Setter
public class TagButtonWidget extends Button {

	public static final Identifier INACTIVE_TEXTURE = MyTotemDoll.id("textures/gui/tag_menu/button_inactive.png");

	public static final WidgetSprites TEXTURES = new WidgetSprites(
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
	private net.minecraft.network.chat.Component tooltipText;
	private boolean canBeHovered = true;

	public TagButtonWidget(Tag tag, int x, int y, TagPressAction pressAction) {
		super(x, y, 14, 14, net.minecraft.network.chat.Component.nullToEmpty(""), (widget) -> pressAction.onPress((TagButtonWidget) widget), Button.DEFAULT_NARRATION);
		this.tag  = tag;
		this.text = String.valueOf(tag.getTag());
		this.icon = TagsManager.getTagIcon(this.text.charAt(0));
	}

	@Override
	public void onPress(InputWithModifiers input) {
		this.pressed = !this.pressed;
		super.onPress(input);
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

	public void setTooltip(@Nullable net.minecraft.network.chat.Component text) {
		this.tooltipText = text;
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
		this.renderPlease(context);
	}

	private void renderPlease(GuiGraphicsExtractor context) {
		this.renderButton(context, this.getX(), this.getY());
		this.requestTooltip();
	}

	protected void renderButton(GuiGraphicsExtractor context, int x, int y) {
		this.renderBackground(context, x, y);
		this.renderIcon(context, x, y);
	}

	protected void renderIcon(GuiGraphicsExtractor context, int x, int y) {
		DrawUtils.drawTexture(context, this.icon, x + (this.getWidth() / 2) - 5, y + (this.getHeight() / 2) - 5, 0, 0, 10, 10, 10, 10);
	}

	protected void renderBackground(GuiGraphicsExtractor context, int x, int y) {
		Identifier texture = !this.active ? INACTIVE_TEXTURE : TEXTURES.get(this.isPressed(), this.isHovered());
		DrawUtils.drawTexture(context, texture, x, y, 0, 0, this.width, this.height, this.width, this.height);
	}

	public void requestTooltip() {
		Minecraft client = Minecraft.getInstance();
		Screen screen = client.screen;

		if (!this.isHovered()) {
			return;
		}

		ClientTooltipComponent component = this.getTooltipComponent();
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

	protected @Nullable ClientTooltipComponent getTooltipComponent() {
		if (this.tooltipText == null) {
			return null;
		}
		return ClientTooltipComponent.create(this.tooltipText.getVisualOrderText());
	}

	public boolean over(double mouseX, double mouseY) {
		return this.active
				&& this.visible
				&& mouseX >= (double) this.getX()
				&& mouseY >= (double) this.getY()
				&& mouseX < (double) (this.getX() + this.getWidth())
				&& mouseY < (double) (this.getY() + this.getHeight());
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
