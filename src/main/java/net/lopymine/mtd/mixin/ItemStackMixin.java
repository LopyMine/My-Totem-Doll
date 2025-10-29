package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.utils.ScreenUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.*;
//? if >=1.21 {
import net.minecraft.item.tooltip.TooltipData;
 //?} else {
/*import net.minecraft.client.item.TooltipData;
*///?}
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.client.MyTotemDollClient;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.TotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.gui.tooltip.combined.CombinedTooltipData;
import net.lopymine.mtd.gui.tooltip.state.LoadingStateTooltipData;
import net.lopymine.mtd.gui.tooltip.tags.*;
import net.lopymine.mtd.gui.tooltip.wrapped.WrappedTextTooltipData;
import net.lopymine.mtd.tag.manager.TagsManager;

import java.util.*;
import java.util.stream.Stream;

@Mixin(ItemStack.class)
@ExtensionMethod(ItemStackExtension.class)
public abstract class ItemStackMixin {

	@Shadow
	public abstract boolean isOf(Item item);

	@ModifyReturnValue(at = @At("RETURN"), method = "getName")
	private Text getName(Text original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled() || !this.isOf(Items.TOTEM_OF_UNDYING)) {
			return original;
		}
		String string = original.getString();
		if (!string.contains("|")) {
			return original;
		}
		String[] data = TagsManager.getDataFromString(string);
		String name = data[0];
		String tags = data[1];
		if (tags == null || name == null) {
			return original;
		}
		return Text.literal(name).setStyle(original.getStyle());
	}

	@ModifyReturnValue(at = @At("RETURN"), method = "getTooltipData")
	private Optional<TooltipData> getTooltipData(Optional<TooltipData> original) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (!TotemDollRenderer.canRender(itemStack)) {
			return original;
		}

		Text customName = itemStack.getRealCustomName();
		if (customName == null) {
			return original;
		}

		String[] data = TagsManager.getDataFromString(customName.getString());

		Optional<TooltipData> loadingStateTooltipData = this.getLoadingStateTooltipData(data);
		Optional<TooltipData> tagsTooltipData = this.getTagsTooltipData(data);

		List<TooltipComponent> list = Stream.of(loadingStateTooltipData, tagsTooltipData)
				.flatMap(Optional::stream)
				.map(TooltipComponent::of)
				.toList();

		return Optional.of(new CombinedTooltipData(list));
	}

	@Unique
	private Optional<TooltipData> getLoadingStateTooltipData(String[] data) {
		Screen currentScreen = MinecraftClient.getInstance().currentScreen;
		if (!(currentScreen instanceof AnvilScreen || ScreenUtils.hasShiftDown())) {
			return Optional.empty();
		}
		if (data.length == 0) {
			return Optional.empty();
		}
		String o = data[0];
		TotemDollData totemDollData = TotemDollManager.getDoll(o);
		return Optional.of(new LoadingStateTooltipData(totemDollData.getStandardSprites().getState()));
	}

	@Unique
	private Optional<TooltipData> getTagsTooltipData(String[] data) {
		if (data.length < 2) {
			return Optional.empty();
		}
		String tags = data[1];
		if (tags == null || tags.isEmpty() || !TagsManager.hasAnyTag(tags)) {
			return Optional.empty();
		}
		return Optional.of(new CombinedTooltipData(
					new WrappedTextTooltipData(MyTotemDoll.text("tags.title").formatted(Formatting.GRAY)),
					new TagsTooltipData(tags)
				)
		);
	}

}
