package illyena.gilding.mixin.block;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * hook for {@link ChiseledBookshelfBlockEntity} {@link GameEventListener}
 */
@Mixin(ChiseledBookshelfBlock.class)
public abstract class ChiseledBookshelfBlockMixin implements BlockEntityProvider {

    @Nullable
    @Shadow public abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(ServerWorld world, T blockEntity) {
        return blockEntity instanceof ChiseledBookshelfBlockEntity bookshelfEntity ? (GameEventListener)bookshelfEntity : null;
    }

}
