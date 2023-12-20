package illyena.gilding.core.item;

import illyena.gilding.core.enchantment.GildingEnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public interface IThunderous {
    public static int durationMultiplier = 10;

    public default void callThunder(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (this.canCallThunder(stack, world, user, remainingUseTicks)) {
            if (!world.isClient) {
                ((ServerWorld)world).setWeather(0, getDuration(stack, world, user, remainingUseTicks), true, true );
            }
        }
    }

    public default boolean canCallThunder(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return GildingEnchantmentHelper.hasThunderous(stack);
    }

    public default int getDuration(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        return (stack.getItem().getMaxUseTime(stack) - remainingUseTicks) * durationMultiplier;
    }

}
