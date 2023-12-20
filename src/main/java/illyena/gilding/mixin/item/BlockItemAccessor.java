package illyena.gilding.mixin.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {

    @Invoker
    BlockState callPlaceFromNbt(BlockPos pos, World world, ItemStack stack, BlockState state);
}
