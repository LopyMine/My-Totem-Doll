package net.lopymine.mtd.yacl.category;

import dev.isxander.yacl3.api.*;
import lombok.experimental.ExtensionMethod;

import net.lopymine.mtd.config.MyTotemDollConfig;
import net.lopymine.mtd.extension.SimpleOptionExtension;
import net.lopymine.mtd.thread.MyTotemDollTaskExecutor;
import net.lopymine.mtd.yacl.custom.simple.main.*;
import net.lopymine.mtd.yacl.custom.simple.utils.SimpleContent;

@ExtensionMethod(SimpleOptionExtension.class)
public class GeneralCategory {

	public static ConfigCategory get(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleCategory.startBuilder("general")
				.groups(getMainGroup(defConfig, config))
				.groups(getThreadGroup(defConfig, config))
				.build();
	}

	private static OptionGroup getMainGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleGroup.startBuilder("main")
				.options(
						SimpleOption.<Boolean>startBuilder("mod_enabled")
								.withBinding(defConfig.isModEnabled(), config::isModEnabled, config::setModEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("support_other_mods_totems")
								.withBinding(defConfig.isSupportOtherModsTotems(), config::isSupportOtherModsTotems, config::setSupportOtherModsTotems, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("debug_log_enabled")
								.withBinding(defConfig.isDebugLogEnabled(), config::isDebugLogEnabled, config::setDebugLogEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build()
				)
				.build();
	}

	public static OptionGroup getThreadGroup(MyTotemDollConfig defConfig, MyTotemDollConfig config) {
		return SimpleGroup.startBuilder("parallel_tasks")
				.options(
						SimpleOption.<Integer>startBuilder("parallel_tasks_count")
								.withBinding(defConfig.getParallelTasksCount(), config::getParallelTasksCount, (i) -> {
									config.setParallelTasksCount(i);
									MyTotemDollTaskExecutor.reload();
								}, false)
								.withDescription(SimpleContent.NONE)
								.withController(1, 50, 1)
								.build()
				)
				.build();
	}

}
