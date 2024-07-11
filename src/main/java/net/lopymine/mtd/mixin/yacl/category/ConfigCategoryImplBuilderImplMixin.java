package net.lopymine.mtd.mixin.yacl.category;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ConfigCategory.Builder;
import dev.isxander.yacl3.impl.ConfigCategoryImpl;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import net.lopymine.mtd.modmenu.yacl.custom.category.impl.*;
import net.lopymine.mtd.utils.mixin.BetterCategoryBuilder;

@Pseudo
@Mixin(ConfigCategoryImpl.BuilderImpl.class)
public class ConfigCategoryImplBuilderImplMixin implements BetterCategoryBuilder {

	@Unique
	private int custom = -1;

	@Dynamic
	@ModifyReturnValue(at = @At("RETURN"), method = "build", remap = false)
	private ConfigCategory swapCategory(ConfigCategory original) {
		if (this.custom == -1) {
			return original;
		} else if (this.custom == 0) {
			return new BetterConfigCategoryImpl(original.name(), original.groups(), original.tooltip());
		} else if (this.custom == 1) {
			return new RenderingConfigCategoryImpl(original.name(), original.groups(), original.tooltip());
		}
		throw new IllegalArgumentException("Who modified me? mm???? [My Totem Doll]");
	}

	@Override
	public Builder myTotemDoll$enableBetter() {
		this.custom = 0;
		return ((Builder) this);
	}

	@Override
	public Builder myTotemDoll$enableRendering() {
		this.custom = 1;
		return ((Builder) this);
	}
}
