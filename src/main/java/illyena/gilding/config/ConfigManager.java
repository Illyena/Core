package illyena.gilding.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import illyena.gilding.GildingClientInit;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.option.BooleanConfigOption;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.EnumConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.io.*;

import static illyena.gilding.GildingInit.LOGGER;
import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class ConfigManager {
    private static File file;

    private static void prepareConfigFile() {
        if (file != null) {
            return;
        }
        file = new File(FabricLoader.getInstance().getConfigDir().toFile(), SUPER_MOD_ID + ".json");
    }

    public static void registerConfigs() {
        FabricLoader.getInstance().getEntrypointContainers("config", Mod.Configs.class).forEach(entrypoint -> {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            String modId = metadata.getId();
            try {
                entrypoint.getEntrypoint();
            } catch (Throwable e) {
                LOGGER.error("Mod {} provides a broken implementation of Configs", modId, e);
            }
        });
    }

    public static void initializeConfig() {
        registerConfigs();
        load();
    }

    private static void load() {
        prepareConfigFile();

        try {
            if (!file.exists()) {
                save();
            }
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                JsonObject json = JsonParser.parseReader(br).getAsJsonObject();

                for (ConfigOption<?> configOption : ConfigOption.CONFIG) {
                    JsonElement jsonElement = json.get(configOption.getId().getNamespace() + "." + configOption.getKey());
                    if (jsonElement != null && jsonElement.isJsonPrimitive()) {
                        JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
                        switch (configOption.getType()) {
                            case INT -> {
                                if (primitive.isNumber()) {
                                    ((IntegerConfigOption) configOption).setValue(primitive.getAsInt());
                                }
                            }
                            case BOOL -> {
                                if (primitive.isBoolean()) {
                                    ((BooleanConfigOption) configOption).setValue(primitive.getAsBoolean());
                                }
                            }
                            case ENUM -> {
                                if (primitive.isString()) {
                                    EnumConfigOption<?> enumConfig = (EnumConfigOption<?>) configOption;
                                    Enum<?> found = enumConfig.getValueFromString(primitive.getAsString());

                                    if (found != null) {
                                        ConfigOption.ConfigOptionStorage.setEnum(enumConfig.getKey(), found);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load Gilding configuration file; reverting to defaults");
            e.printStackTrace();
        }
    }

    public static void save() {
        prepareConfigFile();

        JsonObject config = new JsonObject();

        for (ConfigOption<?> configOption : ConfigOption.CONFIG) {
            String name = configOption.getId().getNamespace() + "." + configOption.getKey();
            switch (configOption.getType()) {
                case INT -> config.addProperty(name, ((IntegerConfigOption) configOption).getValue());
                case BOOL -> config.addProperty(name, ((BooleanConfigOption) configOption).getValue());
                case ENUM -> config.addProperty(name, ((EnumConfigOption<?>) configOption).getValue().name());
                default -> config.addProperty(name, configOption.getValue().toString());
            }
        }

        String jsonString = GildingClientInit.GSON.toJson(config);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.err.println("Couldn't save Gilding configuration file");
            e.printStackTrace();
        }
    }

}
