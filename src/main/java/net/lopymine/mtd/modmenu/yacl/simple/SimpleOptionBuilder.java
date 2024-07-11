package net.lopymine.mtd.modmenu.yacl.simple;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.Option.Builder;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.YACLScreen;

import net.lopymine.mtd.utils.ModMenuUtils;

import java.util.function.*;

public class SimpleOptionBuilder {

	public static Option.Builder<Boolean> getBooleanOptionWithDescription(String optionId, boolean defValue, Supplier<Boolean> getter, Consumer<Boolean> setter, ValueFormatter<Boolean> formatter, SimpleContent content, int contentWidth, int contentHeight) {
		return getOption(optionId, defValue, getter, setter, content, true, contentWidth, contentHeight)
				.controller(o -> BooleanControllerBuilder.create(o).coloured(true).formatValue(formatter));
	}

	public static Option.Builder<Double> getDoubleOptionAsSliderWithoutDescription(String optionId, double min, double max, double step, double defValue, Supplier<Double> getter, Consumer<Double> setter) {
		return getOption(optionId, defValue, getter, setter, SimpleContent.NONE, false, 0, 0)
				.controller(o -> DoubleSliderControllerBuilder.create(o).range(min, max).step(step));
	}

	public static <C> Option.Builder<C> getOption(String optionId, C defValue, Supplier<C> getter, Consumer<C> setter, SimpleContent content, boolean addDescription, int contentWidth, int contentHeight) {
		String optionKey = ModMenuUtils.getOptionKey(optionId);

		Builder<C> optionBuilder = Option.<C>createBuilder()
				.name(ModMenuUtils.getName(optionKey))
				.binding(defValue, getter, setter);

		if (addDescription) {
			OptionDescription.Builder descriptionBuilder = OptionDescription.createBuilder().text(ModMenuUtils.getDescription(optionKey));
			if (content == SimpleContent.IMAGE) {
				descriptionBuilder.image(ModMenuUtils.getContentId(content, optionId), contentWidth, contentHeight);
			}
			if (content == SimpleContent.WEBP) {
				descriptionBuilder.webpImage(ModMenuUtils.getContentId(content, optionId));
			}
			optionBuilder.description(descriptionBuilder.build());
		}

		return optionBuilder;
	}

	public static Option<?> getButtonOption(String optionId, BiConsumer<YACLScreen, ButtonOption> action) {
		String optionKey = ModMenuUtils.getOptionKey(optionId);

		ButtonOption.Builder builder = ButtonOption.createBuilder()
				.name(ModMenuUtils.getName(optionKey))
				.action(action);

		return builder.build();
	}

	public Option<?>[] collect(Option<?>... options) {
		return options;
	}

	public <T> Option<T> getIf(Option<T> option, BooleanSupplier condition) {
		if (condition.getAsBoolean()) {
			return option;
		}
		return null;
	}
}
