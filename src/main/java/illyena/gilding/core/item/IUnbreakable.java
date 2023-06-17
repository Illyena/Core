package illyena.gilding.core.item;

import net.minecraft.item.ItemStack;

public interface IUnbreakable {
    default boolean isUsable(ItemStack stack) { return stack.getDamage() < stack.getMaxDamage() - 1; }
}
