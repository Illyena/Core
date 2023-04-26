package illyena.gilding.compat;

import illyena.gilding.config.gui.PlaceHolderScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static illyena.gilding.GildingInit.LOGGER;

public class Compat {

    public static void registerCompatMods() {
        final CompatMod LITEMATICA_SCREEN = new CompatMod("litematica", "fi.dy.masa.litematica.gui.GuiMainMenu", new String[] {});
    }

    static ModContainer getModContainer(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElse(null);
    }

    static <T> T getEntryPoint(String key, Class<T> type,  String modId) {
        for (EntrypointContainer<T> container : FabricLoader.getInstance().getEntrypointContainers(key, type)) {
            if (container.getProvider().equals(getModContainer(modId))) {
                return container.getEntrypoint();
            }
        }
        return null;
    }

    public static class CompatMod extends Mod {
        private final String screenClass;
        private final String[] parameters;


        public CompatMod(String modId, String screenClass, String[] parameters) {
            super(modId, null, !(getModContainer(modId).getContainedMods().isEmpty()), null);
            this.screenClass = screenClass;
            this.parameters = parameters;
        }

        public String getScreenClass() { return this.screenClass; }

        public String[] getParameters() { return this.parameters; }

        private static Class<?> getClass(String className) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private static Class<?>[] getParamClasses(String[] parameters) {
            List<Class<?>> paramClasses = new ArrayList<>();
            for (String param : parameters) {
                paramClasses.add(getClass(param));
            }
            return paramClasses.toArray(new Class<?>[]{});
        }

        @Environment(EnvType.CLIENT)
        @Override
        public Screen getScreen(Screen parent) {
            ModInitializer entrypoint = getEntryPoint("main", ModInitializer.class, this.getModId());
            Screen screen = new PlaceHolderScreen(this.getModId(), parent);
            try {
                Class<? extends Screen> clazz = entrypoint.getClass().getClassLoader().loadClass(this.getScreenClass()).asSubclass(Screen.class);
                if (this.getParameters().length == 1 && getParamClasses(this.getParameters())[0].isAssignableFrom(Screen.class)) {
                    screen = clazz.getConstructor(getParamClasses(this.getParameters())).newInstance(parent);
                } else {
                    screen = clazz.getConstructor().newInstance();
                }
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                         IllegalAccessException | NoSuchMethodException | NullPointerException e) {
                LOGGER.error("UNABLE TO LOAD {} SCREEN", this.getModId().toUpperCase()); //todo
            }


            return screen;
        }

        @Environment(EnvType.CLIENT)
        public static class CompatScreens extends ModScreens {
            public static void registerCompatModScreens() {
                Mod.MODS.forEach(mod -> {
                    if (mod instanceof CompatMod compatMod) {
                        ModScreens.registerConfigScreen(mod.getModId(), compatMod.getScreen(MinecraftClient.getInstance().currentScreen));
                    }
                });
            }

        }



    /*
        FabricLoader fabricLoader = FabricLoader.getInstance();
    fabricLoader.getEntrypointContainers(Identifier.of(MODID, "event_bus").toString(), Object.clreplaced).forEach(EntrypointManager::setup);
    fabricLoader.getEntrypointContainers(Identifier.of(MODID, "event_bus_" + fabricLoader.getEnvironmentType().name().toLowerCase()).toString(), Object.clreplaced).forEach(EntrypointManager::setup);
        Collection<ModContainer> mods = fabricLoader.getAllMods();
    LOGGER.info("Loading replacedets...");
    ResourceManager.findResources(MODID + "/recipes", file -> file.endsWith(".json")).forEach(recipe -> {
            try {
                String rawId = new Gson().fromJson(new InputStreamReader(recipe.openStream()), JsonRecipeType.clreplaced).getType();
                try {
                    Identifier recipeId = Identifier.of(rawId);
                    JsonRecipesRegistry.INSTANCE.computeIfAbsent(recipeId, identifier -> new HashSet<>()).add(recipe);
                } catch (NullPointerException e) {
                    LOGGER.warn("Found an unknown recipe type " + rawId + ". Ignoring.");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    mods.forEach(modContainer -> {
            net.modificationstation.stationapi.api.common.registry.ModID modID = net.modificationstation.stationapi.api.common.registry.ModID.of(modContainer);
            String pathName = "/replacedets/" + modID + "/" + MODID + "/lang";
            URL path = getClreplaced().getResource(pathName);
            if (path != null) {
                I18n.addLangFolder(modID, pathName);
                LOGGER.info("Registered lang path");
            }
        });
    LOGGER.info("Gathering mods that require client verification...");
        String value = MODID + ":verify_client";
    mods.forEach(modContainer -> {
            ModMetadata modMetadata = modContainer.getMetadata();
            if (modMetadata.containsCustomValue(value) && modMetadata.getCustomValue(value).getAsBoolean())
                modsToVerifyOnClient.add(modContainer);
        });
    LOGGER.info("Invoking PreInit event...");
    EVENT_BUS.post(new PreInitEvent());
    LOGGER.info("Invoking Init event...");
    EVENT_BUS.post(new InitEvent());
    LOGGER.info("Invoking PostInit event...");
    EVENT_BUS.post(new PostInitEvent());
    }
    */
    }
}
