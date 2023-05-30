package illyena.gilding.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class Mod {
    public static final SimpleRegistry<Mod> MODS = FabricRegistryBuilder.createSimple(Mod.class, new Identifier(SUPER_MOD_ID, "mods")).attribute(RegistryAttribute.SYNCED).attribute(RegistryAttribute.PERSISTED).buildAndRegister();
    private final String modId;
    private final Mod parent;
    private final boolean isSubGroupParent;
    private final Class<?> configClass;

    public Mod(String modId, @Nullable Mod parent, boolean subGroupParent, @Nullable Class<?> configClass) {
        this.modId = modId;
        this.parent = parent;
        this.isSubGroupParent = subGroupParent;
        this.configClass = configClass;

        Registry.register(MODS, new Identifier(SUPER_MOD_ID, modId), this);
    }

    public String getModId() { return this.modId; }

    public boolean isLoaded() {
        if (this.isSubGroupParent && getModsWithSubGroups(this.getModId()).isEmpty()) {
            return false;
        } else return FabricLoader.getInstance().isModLoaded(this.modId);
    }

    public Mod getParentMod() { return this.parent; }

    public Class<?> getConfigClass() { return this.configClass; }

    @Environment(EnvType.CLIENT)
    private Screen getScreen(Screen parent) {
        return ModScreens.SCREENS.get(new Identifier(SUPER_MOD_ID, this.modId));
    } //todo implement parent



    //BY MOD_ID

    /**
     * @param modId identifies Mod
     * @return the registered Mod with modId @param modId
     */
    public static Mod getFromId(String modId) { return MODS.get(new Identifier(SUPER_MOD_ID, modId)); }

    /**
     * @param modId identifies Mod
     * @return a boolean of whether the mod is loaded or not.
     */
    public static boolean isLoaded(String modId) { return getFromId(modId).isLoaded(); }

    /**
     * @param modId identifies Mod
     * @return the registered parent Mod of the register mod with modId @param modId
     */
    public static Mod getPartentMod(String modId) { return getFromId(modId).getParentMod();}


    /**
     * @param modId identifies Mod
     * @return the class holding this @param modId 's ConfigOptions.
     */
    public static Class<?> getConfigClass(String modId) { return getFromId(modId).getConfigClass(); }

    /**
     * @param modId identifies Mod
     * @return the Fabric ModContainer of this @param modId
     */
    public static ModContainer getModContainer(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElse(null);
    }

    //LISTS

    /**
     * @return List of all register modIds
     */
    public static Collection<String> getModIds() {
        List<String> list = new ArrayList<>();
        MODS.getIds().forEach(id -> list.add(id.getPath()));
        return list;
    }

    /**
     * @return List of all currently loaded and registered modIds
     */
    public static List<String> loadedModIds() {
        List<String> loadedModIds = new ArrayList<>();
        MODS.forEach((mod -> {
            if (FabricLoader.getInstance().isModLoaded(mod.getModId())) {
                loadedModIds.add(mod.getModId());
            }
        }));
        return loadedModIds;
    }

    /**
     * @param modId identifies Mod
     * @return a List of all registered Mods with the identified Mod as their parent excluding subMods
     */
    public static List<Mod> getModsSansSubGroups(String modId) {
        List<Mod> mods = new ArrayList<>();
        for (Mod mod : MODS) {
            if (mod.getParentMod() != null && mod.getParentMod().getModId().equals(modId)) {
                mods.add(mod);
            }
        }
        return mods;
    }

    /**
     * @param modId identifies Mod
     * @return List of all Mods with Mod as mod.parent and its subMods;
     */
    public static List<Mod> getModsWithSubGroups(String modId) {
        List<Mod> mods = getModsSansSubGroups(modId);
        List<Mod> parentMods = MODS.stream().filter(mod -> mod.isSubGroupParent).toList();

        int i = parentMods.size();
        do {
            for (Mod parentMod : parentMods) {
                if (parentMod.getParentMod() != null && (parentMod.getParentMod().equals(getFromId(modId)) || mods.contains(parentMod.getParentMod()))) {
                    mods.addAll(getModsSansSubGroups(parentMod.getModId()));
                }
            }
            --i;
        } while (i > 0);

       return mods;
    }

    /**
     * @param modId identifies excluded Mod group
     * @return a List of loaded ModContainers excluding registered Mods with a parent of identified Mod and their subMods.
     */
    public static List<ModContainer> getOtherModContainers(String modId) {
        List<String> ids = new ArrayList<>();
        for (Mod mod : getModsWithSubGroups(modId)) {
            ids.add(mod.getModId());
        }
        return FabricLoader.getInstance().getAllMods().stream().filter(container -> !ids.contains(container.getMetadata().getId())).toList();
    }

    /**
     * @param modId identifies excluded Mod group
     * @return List of registered Mods excluding registered Mods with a parent of identified Mod and their subMods.
     */
    public static List<Mod> getOtherMods(String modId) {
        List<Mod> mods = new ArrayList<>();
        for (Mod mod : MODS) {
            if (!getModsWithSubGroups(modId).contains(mod)) {
                mods.add(mod);
            }
        }
        return mods;
    }

    /**
     * @param modId identifies ModContainer
     * @return String of Mod's version.
     */
    public static String getModVersion (String modId) {
        return getModContainer(modId) == null ? "" : getModContainer(modId).getMetadata().getVersion().getFriendlyString();
    }

    /**
     * @param modId identifies ModContainer
     * @return String of combined gameVersion and modVersion.
     */
    public static String getVersion(String modId) {
        return SharedConstants.getGameVersion().getName() + " v:" + getModContainer(modId).getMetadata().getVersion();
    }



    // RUN METHODS

    /**
     * Simple hook to run code if a mod is installed
     * @author Fabric
     * @param toRun will be run only if the mod is loaded
     * @return Optional.empty() if the mod is not loaded, otherwise an Optional of the return value of the given supplier
     */
    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        if (isLoaded())
            return Optional.of(toRun.get().get());
        return Optional.empty();
    }

    /**
     * Simple hook to execute code if a mod is installed
     * @author Fabric
     * @param toExecute will be executed only if the mod is loaded
     */
    public void executeIfInstalled(Supplier<Runnable> toExecute) {
        if (isLoaded()) {
            toExecute.get().run();
        }
    }

    /**
     * Client side only class that separately registers Mod config Screens
     * to avoid the Server attempting to call Screen.
     */
    @Environment(EnvType.CLIENT)
    public static class ModScreens {
        public static Map<String, Class<? extends Screen>> map = new HashMap<>();
        private static final SimpleRegistry<Screen> SCREENS = FabricRegistryBuilder.createSimple(Screen.class, new Identifier(SUPER_MOD_ID, "screens")).buildAndRegister();

        /**
         * Registers a new instance of a Mod's config Screen in the SCREENS registry
         * so it can be accessed via @param modId
         * @param modId identifies Mod
         * @param screen new instance of config Screen
         * @return the @param screen that was passed in
         */
        public static Screen registerConfigScreen(String modId, Screen screen) {
            map.put(modId, screen.getClass());
            Registry.register(SCREENS, new Identifier(SUPER_MOD_ID, modId), screen);
            return screen;
        }

        /**
         * @param modId identifies Mod
         * @param parent instance of the previous Screen
         * @return a new instance of a Mod's config Screen by its modId
         */
        public static Screen getScreen(String modId, Screen parent) {
            try {
                return map.get(modId).getConstructor(Screen.class).newInstance(parent);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                return getFromId(modId).getScreen(parent);
            }
        }

    }


} //todo protect from NullPointerException
