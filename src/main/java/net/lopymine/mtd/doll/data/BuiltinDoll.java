package net.lopymine.mtd.doll.data;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record BuiltinDoll(char tag, @NotNull Identifier id, boolean animated) {

}
