package illyena.gilding.core.block.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/** <pre>
 * INSERT if not extending FallingBlock
 *
 * insert field:
 *      {@code private FallingBlockEntity fallingBlockEntity}
 *
 * insert at #onBlockAdded, and #getStateForNeighborUpdate
 *      {@code world.scheduleBlockTick(pos, this, this.getFallDelay();}
 *
 * insert at #scheduledTick
 *      {@code if (canFallThrough(world.getBlockState(pos.down())) &&
 *          pos.getY() > world.getBottomY()) {
 *             this.fallingBlockEntity =
 *                  FallingBlockEntity.spawnFromBlock(world, pos, state);
 *             this.configureFallingBlockEntity();
 *         }}
 *
 * insert at #randomDisplayTick
 *      {@code LimitedFallingBlock.super.randomDisplayTick(state, world, pos, random);}
 *  </pre>
 */
@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface LimitedFallingBlock {

    public abstract void configureFallingBlockEntity(FallingBlockEntity fallingBlockEntity);

    public abstract int getFallDelay();

    public abstract boolean limit();

    public abstract void action();

    public default void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockPos = pos.down();
            if (this.canFallThrough(world.getBlockState(blockPos))) {
                double d = (double)pos.getX() + random.nextDouble();
                double e = (double)pos.getY() - 0.05;
                double f = (double)pos.getZ() + random.nextDouble();
                world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    public default boolean canFallThrough(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.getMaterial().isReplaceable()|| state.getMaterial().isLiquid();
    }

    public static ItemStack asItemStack(FallingBlockEntity blockEntity) {
        ItemStack stack = new ItemStack(blockEntity.getBlockState().getBlock());
        if (blockEntity.blockEntityData != null && !blockEntity.blockEntityData.isEmpty()) {
            NbtCompound nbt = blockEntity.blockEntityData;
            if (nbt.contains("Enchantments")) {
                NbtCompound nbtCompound = new NbtCompound();
                NbtElement nbtElement = nbt.get("Enchantments");
                nbtCompound.put("Enchantments", nbtElement);
                stack.setNbt(nbtCompound);
            }
            if (nbt.contains("Damage")) {
                stack.setDamage(nbt.getInt("Damage"));
            }
            if (nbt.contains("CustomName")) {
                stack.setCustomName(Text.Serializer.fromJson(nbt.getString("CustomName")));
            }
        }
        return stack;
    }

}
