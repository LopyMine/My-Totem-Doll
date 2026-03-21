package net.lopymine.mtd.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.experimental.ExtensionMethod;
import net.lopymine.mtd.MyTotemDoll;
import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.doll.data.TotemDollData;
import net.lopymine.mtd.doll.manager.TotemDollManager;
import net.lopymine.mtd.doll.renderer.TotemDollRenderer;
import net.lopymine.mtd.extension.ItemStackExtension;
import net.lopymine.mtd.gui.tooltip.combined.CombinedTooltipData;
import net.lopymine.mtd.gui.tooltip.state.LoadingStateTooltipData;
import net.lopymine.mtd.gui.tooltip.tags.TagsTooltipData;
import net.lopymine.mtd.gui.tooltip.wrapped.WrappedTextTooltipData;
import net.lopymine.mtd.tag.manager.TagsManager;
import net.lopymine.mtd.utils.ScreenUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
@ExtensionMethod(ItemStackExtension.class)
public abstract class ItemStackMixin {

	@Shadow public abstract boolean is(Predicate<Holder<Item>> predicate);

	@ModifyReturnValue(at = @At("RETURN"), method = "getHoverName")
	private Component getName(Component original) {
		if (!MyTotemDollConfig.getInstance().isModEnabled() || !this.is((holder) -> holder.is(Items.TOTEM_OF_UNDYING.builtInRegistryHolder().key()))) {
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
		return Component.literal(name).setStyle(original.getStyle());
	}

	@ModifyReturnValue(at = @At("RETURN"), method = "getTooltipImage")
	private Optional<TooltipComponent> getTooltipData(Optional<TooltipComponent> original) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (!TotemDollRenderer.canRender(itemStack)) {
			return original;
		}

		Component customName = itemStack.getRealCustomName();
		if (customName == null) {
			return original;
		}

		String[] data = TagsManager.getDataFromString(customName.getString());

		Optional<TooltipComponent> loadingStateTooltipData = this.getLoadingStateTooltipData(data);
		Optional<TooltipComponent> tagsTooltipData = this.getTagsTooltipData(data);

		List<ClientTooltipComponent> list = Stream.of(loadingStateTooltipData, tagsTooltipData)
				.flatMap(Optional::stream)
				.map(ClientTooltipComponent::create)
				.toList();

		return Optional.of(new CombinedTooltipData(list));
	}

	@Unique
	private Optional<TooltipComponent> getLoadingStateTooltipData(String[] data) {
		Screen currentScreen = Minecraft.getInstance().screen;
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
	private Optional<TooltipComponent> getTagsTooltipData(String[] data) {
		if (data.length < 2) {
			return Optional.empty();
		}
		String tags = data[1];
		if (tags == null || tags.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new CombinedTooltipData(
						new WrappedTextTooltipData(MyTotemDoll.text("tags.title").withStyle(ChatFormatting.GRAY)),
						new TagsTooltipData(tags)
				)
		);
	}

}
