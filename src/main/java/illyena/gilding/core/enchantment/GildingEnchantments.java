package illyena.gilding.core.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.*;

public class GildingEnchantments {

    public static void callEnchantments() { LOGGER.info("Registering enchantments for {} mod.", SUPER_MOD_NAME); }

    public static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(Registries.ENCHANTMENT, new Identifier(SUPER_MOD_ID, name), enchantment);
    }

    public static final Enchantment RICOCHET = registerEnchantment("ricochet",
            new RicochetEnchantment(Enchantment.Rarity.RARE, GildingEnchantmentTarget.THROWABLE_TARGET, EquipmentSlot.MAINHAND));
    public static final Enchantment THUNDEROUS = registerEnchantment("thunderous",
            new ThunderousEnchantment(Enchantment.Rarity.RARE, GildingEnchantmentTarget.THUNDEROUS_TARGET, EquipmentSlot.OFFHAND));

}
