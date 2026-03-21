package net.lopymine.mtd.mixin;

import net.lopymine.mtd.utils.mixin.ItemStackWithPlayerEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;


@Mixin(ItemStack.class)
public class ItemStackWithPlayerEntityMixin implements ItemStackWithPlayerEntity {

	@Unique
	private AbstractClientPlayer player;

	@Override
	public void myTotemDoll$setPlayerEntity(AbstractClientPlayer player) {
		this.player = player;
	}

	@Override
	public AbstractClientPlayer myTotemDoll$getPlayerEntity() {
		return this.player;
	}
}
