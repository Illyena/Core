package illyena.gilding.core.loot.condition;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.*;

public class GildingLootConditionTypes {

    public static void callLootConditions() { LOGGER.info("Registering loot conditions for {} mod.", SUPER_MOD_NAME); }

    public static LootConditionType registerLootCondition(String name, LootConditionType type) {
        return Registry.register(Registries.LOOT_CONDITION_TYPE, new Identifier(SUPER_MOD_ID, name), type);
    }

    public static final LootConditionType CONFIG_PROPERTIES = registerLootCondition("config_properties",
            new LootConditionType(new ConfigPropertiesLootCondition.Serializer()));

}
