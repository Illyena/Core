package illyena.gilding.core.predicate;

import com.google.gson.*;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.EnumConfigOption;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ConfigPredicate<T> {
    public static final ConfigPredicate<?> ANY = new ConfigPredicate<>();
    @Nullable
    private final ConfigOption<T> config;
    private final T value;

    public ConfigPredicate() {
         this(null, null);
    }

    private ConfigPredicate(@Nullable ConfigOption<T> config, T value) {
        this.config = config;
        this.value = value;
    }

    public boolean test() {
        Map<ConfigOption<T>, T> configs = new HashMap<>();
        ConfigOption.CONFIG.stream().forEach(configOption -> configs.put(configOption, (T)configOption.getValue()));

        if (this.config != null) {
            if (!configs.containsKey(this.config)) {
                return false;
            }
            switch (this.config.getType()) {
                case INT -> {
                    int i = (Integer)configs.get(this.config);
                    return this.value == NumberRange.IntRange.ANY || ((NumberRange.IntRange)this.value).test(i);
                }
                case BOOL -> {
                    boolean bool = (Boolean)configs.get(this.config);
                    return this.value.equals(bool);
                }
                case ENUM -> {
                    EnumConfigOption<?> enumConfig = (EnumConfigOption<?>)this.config;
                    return enumConfig.getValueFromString(this.value.toString()).equals(enumConfig.getValue());
                }
            }
        }
        return true;
    }

    public JsonElement serialize() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonObject = new JsonObject();
            if (this.config != null) {
                jsonObject.addProperty("config", ConfigOption.CONFIG.getId(this.config).toString());
                switch (this.config.getType()) {
                    case INT -> jsonObject.add("value", ((NumberRange.IntRange) this.value).toJson());
                    case BOOL -> jsonObject.add("value", new JsonPrimitive((Boolean) this.value));
                    case ENUM -> jsonObject.add("value", new JsonPrimitive(((EnumConfigOption<?>) this.config).getValueFromString(this.value.toString()).name()));
                }
            }
            return jsonObject;
        }
    }

    public static ConfigPredicate<?> deserialize(@Nullable JsonElement jsonElement) {
        if (jsonElement != null && !jsonElement.isJsonNull()) {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "config");
            ConfigOption<?> configOption;
            if (jsonObject.has("config")) {
                Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "config"));
                configOption = ConfigOption.CONFIG.getOrEmpty(identifier).orElseThrow(() ->
                        new JsonSyntaxException("Unknown configuration '" + identifier + "'"));
                switch (configOption.getType()) {
                    case INT -> {
                        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("value"));
                        return new ConfigPredicate(configOption, intRange);
                    }
                    case BOOL -> {
                        if (jsonObject.get("value").isJsonPrimitive() && jsonObject.getAsJsonPrimitive("value").isBoolean()) {
                            return new ConfigPredicate(configOption, jsonObject.getAsJsonPrimitive("value").getAsBoolean());
                        }
                    }
                    case ENUM -> {
                        return new ConfigPredicate(configOption, ((EnumConfigOption<?>)configOption).getValueFromString(jsonObject.getAsJsonPrimitive("value").getAsString()));

                    }
                }
            }
        } return ANY;
    }

}
