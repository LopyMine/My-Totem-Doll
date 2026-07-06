package net.lopymine.mtd.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.*;
import net.minecraft.util.profiling.ProfilerFiller;

public class ProfilerUtils {

	public static ProfilerFiller getProfiler() {
		 return Minecraft.getInstance().getProfiler();
	}

}
