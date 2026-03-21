package net.lopymine.mtd.mixin.modmenu;

import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import java.util.*;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.lopymine.mtd.MyTotemDoll;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(FabricMod.class)
public class FabricModMixin {

	@Unique
	private static final Map<String, List<String>> MODEL_AUTHORS = Map.of(
			"Kreo_gen", List.of("gnom", "mini_3d", "parrot", "player_bucket", "pots", "rat", "stairs", "wheelchair")
	);
	@Shadow(remap = false)
	@Final
	protected ModMetadata metadata;

	@Dynamic
	@Inject(at = @At("RETURN"), method = "getContributors", remap = false)
	private void addMoreContributors(CallbackInfoReturnable<Map<String, Collection<String>>> cir) {
		if (!MyTotemDoll.MOD_ID.equals(this.metadata.getId())) {
			return;
		}
		Map<String, Collection<String>> map = cir.getReturnValue();
		MODEL_AUTHORS.forEach((nickname, models) -> {
			this.addBuiltinCustomModelAuthor(map, nickname, models);
		});
	}

	@Unique
	private void addBuiltinCustomModelAuthor(Map<String, Collection<String>> map, @SuppressWarnings("all") String nickname, List<String> models) {
		map.put(nickname + " " + Arrays.toString(models.toArray()), List.of("Community Model Author"));
	}
}
