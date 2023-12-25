package illyena.gilding.core.item.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * limit to {@link BlockItem} descendants.
 */
@SuppressWarnings("UnnecessaryModifier")
public interface BlockEntityItem {

    public abstract boolean toBlock(ItemStack stack, World world, Entity entity, BlockPos blockPos, int radius);

}
