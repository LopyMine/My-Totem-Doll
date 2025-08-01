package net.lopymine.mtd.mixin.modmenu;

import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import java.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(FabricMod.class)
public class FabricModMixin {

	@Dynamic
	@Inject(at = @At("RETURN"), method = "getContributors", remap = false)
	private void addMoreContributors(CallbackInfoReturnable<Map<String, Collection<String>>> cir) {
		Map<String, Collection<String>> map = cir.getReturnValue();
		this.addBuiltinCustomModelAuthor(map, "Kreo_gen", List.of("gnom", "mini_3d", "parrot", "player_bucket", "pots", "rat", "stairs", "wheelchair"));
	}

	@Unique
	private void addBuiltinCustomModelAuthor(Map<String, Collection<String>> map, @SuppressWarnings("all") String nickname, List<String> list) {
		map.put(nickname + " " + Arrays.toString(list.toArray()), List.of("Model Author"));
	}

}
