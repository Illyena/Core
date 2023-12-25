package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ThunderousEnchantment extends Enchantment {
    public ItemStack thunderingItem;

    public ThunderousEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot... slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    protected boolean canAccept(Enchantment other) { return super.canAccept(other); }

    @Override
    public boolean isTreasure() { return true; }

    @Override
    public boolean isAvailableForEnchantedBookOffer() { return false; }

    @Override
    public boolean isAvailableForRandomSelection() { return true; }

}