package net.lopymine.mtd.modmenu.yacl;

import net.minecraft.util.Identifier;

import net.lopymine.mtd.MyTotemDoll;

public class TransparencySprites {

	public static final SpriteTextures TAB_BUTTON_SPRITES = new SpriteTextures(
			MyTotemDoll.spriteId("transparency/tab/tab_selected"), // enabled
			MyTotemDoll.spriteId("transparency/tab/tab"), // disabled
			MyTotemDoll.spriteId("transparency/tab/tab_selected_highlighted"), // enabled and hovered
			MyTotemDoll.spriteId("transparency/tab/tab_highlighted") // disabled and hovered
			);

	public static final SpriteTextures WIDGET_SPRITES = new SpriteTextures(
			MyTotemDoll.spriteId("transparency/widget"), // enabled
			MyTotemDoll.spriteId("transparency/disabled_widget"), // disabled
			MyTotemDoll.spriteId("transparency/hovered_widget"), // enabled and hovered
			MyTotemDoll.spriteId("transparency/disabled_widget") // disabled and hovered
	);

	public static final Identifier SCROLLER_SPRITE = MyTotemDoll.spriteId("transparency/scroller/scroller");
	public static final Identifier SCROLLER_BACKGROUND_SPRITE = MyTotemDoll.spriteId("transparency/scroller/scroller_background");

	public static final Identifier MENU_BACKGROUND_TEXTURE_V2 = MyTotemDoll.id("textures/gui/transparency/menu_background_v2.png");
	public static final Identifier MENU_BACKGROUND_TEXTURE = MyTotemDoll.id("textures/gui/transparency/menu_background.png");
	public static final Identifier MENU_LIST_BACKGROUND_TEXTURE = MyTotemDoll.id("textures/gui/transparency/menu_list_background.png");
	public static final Identifier HEADER_SEPARATOR_TEXTURE = MyTotemDoll.id("textures/gui/transparency/header_separator.png");
	public static final Identifier FOOTER_SEPARATOR_TEXTURE = MyTotemDoll.id("textures/gui/transparency/footer_separator.png");

	public record SpriteTextures(Identifier enabled, Identifier disabled, Identifier enabledFocused,
	                             Identifier disabledFocused) {

		public Identifier get(boolean enabled, boolean hovered) {
			return enabled ?
					(
							!hovered ?
									this.enabled
									:
									this.enabledFocused
					)
					:
					(
							!hovered ?
									this.disabled
									:
									this.disabledFocused
					);
		}

	}
}
