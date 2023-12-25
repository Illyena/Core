package illyena.gilding.mixin.enchantment;

import illyena.gilding.core.item.IThrowable;
import net.minecraft.enchantment.PowerEnchantment;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("unused")
@Mixin(PowerEnchantment.class)
public class PowerEnchantmentMixin {

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof IThrowable || stack.getItem() instanceof BowItem;
    }

}
