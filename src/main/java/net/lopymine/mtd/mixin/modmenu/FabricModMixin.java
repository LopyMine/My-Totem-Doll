package net.lopymine.mtd.mixin.modmenu;

//? if fabric {

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

	@Shadow(remap = false) @Final protected ModMetadata metadata;
	@Unique
	private static final Map<String, List<String>> MODEL_AUTHORS = Map.of(
			"Kreo_gen", List.of("gnom", "mini_3d", "parrot", "player_bucket", "pots", "rat", "stairs", "wheelchair")
	);

	@Inject(at = @At("RETURN"), method = "getContributors", remap = false)
	private void addMoreContributors(CallbackInfoReturnable<List<String>> cir) {
		if (!MyTotemDoll.MOD_ID.equals(this.metadata.getId())) {
			return;
		}
		List<String> list = cir.getReturnValue();
		list.add(" ");
		list.add("Community Model Authors");
		MODEL_AUTHORS.forEach((nickname, models) -> {
			list.add(nickname + " " + Arrays.toString(models.toArray()));
		});
	}

}

//?}
