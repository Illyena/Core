package illyena.gilding.config.option;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import illyena.gilding.config.network.ConfigNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

import java.util.*;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public abstract class ConfigOption<T> {
    public static final SimpleRegistry<ConfigOption> CONFIG = FabricRegistryBuilder.createSimple(ConfigOption.class, new Identifier(SUPER_MOD_ID, "config"))
            .attribute(RegistryAttribute.SYNCED).attribute(RegistryAttribute.PERSISTED).buildAndRegister();
    private boolean dirty;
    protected Type type;
    protected AccessType accessType;
    protected final String key;
    protected final String modId;
    protected final Identifier id;

    public ConfigOption(String modId, String key, AccessType accessType) {
        this.key = key;
        this.modId = modId;
        this.id = new Identifier(modId, key.toLowerCase());
        this.accessType = accessType;
        Registry.register(CONFIG, id, this);
    }

    public static ConfigOption<?> getConfig(Identifier id) { return CONFIG.get(id); }

    public static List<ConfigOption<?>> getConfigs(String modId) {
        List<ConfigOption<?>> list = new ArrayList<>();
        for (ConfigOption<?> config : CONFIG) {
            if (config.modId.equals(modId)) {
                list.add(config);
            }
        }
        return list;
    }

    public Identifier getId() { return this.id; }

    public String getKey() { return this.key; }

    public Type getType() { return this.type; }

    public AccessType getAccessType() { return this.accessType; }

    public abstract T getDefaultValue();

    public abstract void setValue(T value);

    public abstract void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;

    public abstract T getValue();

    public abstract Text getValueText();


    public void markDirty() { this.setDirty(true); }

    private void setDirty(boolean dirty) { this.dirty = dirty; }

    boolean isDirty() { return this.dirty; }

    public abstract Text getButtonText();

    public abstract ClickableWidget createButton(int x, int y, int width);

    public void sync() {
        if (this.isDirty()) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeIdentifier(this.id);
            switch (this.type) {
                case INT -> data.writeInt((Integer) this.getValue());
                case BOOL -> data.writeBoolean((Boolean) this.getValue());
                case ENUM -> data.writeEnumConstant((Enum<?>) this.getValue());
            }
            ClientPlayNetworking.send(ConfigNetworking.CONFIG_SYNC_C2S, data);
            this.setDirty(false);
        }
    }

    public void sync(ServerCommandSource source) { this.sync(source.getServer()); }

    public void sync(MinecraftServer server) {
        if (this.isDirty()) {
            PacketByteBuf data = PacketByteBufs.create();
            data.writeIdentifier(this.id);
            switch (this.type) {
                case INT -> data.writeInt((Integer) this.getValue());
                case BOOL -> data.writeBoolean((Boolean) this.getValue());
                case ENUM -> data.writeEnumConstant((Enum<?>) this.getValue());
            }

            for (ServerPlayerEntity player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, ConfigNetworking.CONFIG_SYNC_S2C, data);
            }
            this.setDirty(false);
        }

    }

    public enum Type {
        INT,
        BOOL,
        ENUM;

        Type() { }

    }

    public enum AccessType {
        SERVER,
        CLIENT,
        BOTH,
        WORLD_GEN;

        AccessType() { }
    }

    public static class ConfigOptionStorage {
        private static final Map<String, Boolean> BOOLEAN_OPTIONS = new HashMap<>();
        private static final Map<String, Enum<?>> ENUM_OPTIONS = new HashMap<>();
        private static final Map<String, Set<String>> STRING_SET_OPTIONS = new HashMap<>();
        private static final Map<String, Integer> INTEGER_OPTIONS = new HashMap<>();

        public static void setStringSet(String key, Set<String> value) { STRING_SET_OPTIONS.put(key, value); }

        public static Set<String> getStringSet(String key) { return STRING_SET_OPTIONS.get(key); }

        public static void setInteger(String key, int value) { INTEGER_OPTIONS.put(key, value); }

        public static int getInteger(String key) { return INTEGER_OPTIONS.get(key); }

        public static void setBoolean(String key, boolean value) { BOOLEAN_OPTIONS.put(key, value); }

        public static void toggleBoolean(String key) { setBoolean(key, !getBoolean(key)); }

        public static boolean getBoolean(String key) { return BOOLEAN_OPTIONS.get(key); }

        @SuppressWarnings("unchecked")
        public static <E extends Enum<E>> E getEnum(String key, Class<E> typeClass) {
            return (E) ENUM_OPTIONS.get(key);
        }

        public static Enum<?> getEnumTypeless(String key, Class<Enum<?>> typeClass) {
            return ENUM_OPTIONS.get(key);
        }

        public static <E extends Enum<E>> void setEnum(String key, Enum<E> value) {
            ENUM_OPTIONS.put(key, value);
        }

        public static void setEnumTypeless(String key, Enum<?> value) {
            ENUM_OPTIONS.put(key, value);
        }

        public static <E extends Enum<E>> E cycleEnum(String key, Class<E> typeClass) {
            return cycleEnum(key, typeClass, 1);
        }

        public static <E extends Enum<E>> E cycleEnum(String key, Class<E> typeClass, int amount) {
            E[] values = typeClass.getEnumConstants();
            E currentValue = getEnum(key, typeClass);
            E newValue = values[(currentValue.ordinal() + amount) % values.length];
            setEnum(key, newValue);
            return newValue;
        }

    }
}
