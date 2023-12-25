package illyena.gilding.core.loot.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import illyena.gilding.core.predicate.ConfigPredicate;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

public class ConfigPropertiesLootCondition<T> implements LootCondition {
    final ConfigPredicate<T> predicate;

    public ConfigPropertiesLootCondition(ConfigPredicate<T> predicate) {
        this.predicate = predicate;
    }

    public LootConditionType getType() { return GildingLootConditionTypes.CONFIG_PROPERTIES; }

    public boolean test(LootContext lootContext) {
        return this.predicate.test();
    }

    public static class Serializer implements JsonSerializer<ConfigPropertiesLootCondition<?>> {

        public Serializer() { }

        public void toJson(JsonObject jsonObject, ConfigPropertiesLootCondition lootCondition, JsonSerializationContext context) {
            jsonObject.add("predicate", lootCondition.predicate.serialize());
        }

        public ConfigPropertiesLootCondition<?> fromJson(JsonObject jsonObject, JsonDeserializationContext context) {
            ConfigPredicate<?> configPredicate = ConfigPredicate.deserialize(jsonObject.get("predicate"));
            return new ConfigPropertiesLootCondition<>(configPredicate);
        }

    }

}
