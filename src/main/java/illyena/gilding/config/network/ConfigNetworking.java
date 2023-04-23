package illyena.gilding.config.network;

import illyena.gilding.config.option.BooleanConfigOption;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.EnumConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class ConfigNetworking {
    public static final Identifier CONFIG_RETRIEVE_C2S = new Identifier(SUPER_MOD_ID, "config_retrieve_c2s");
    public static final Identifier CONFIG_SYNC_C2S = new Identifier(SUPER_MOD_ID, "config_sync_c2s");
    public static final Identifier CONFIG_SYNC_S2C = new Identifier(SUPER_MOD_ID, "config_sync_s2c");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(CONFIG_SYNC_C2S, ConfigSyncC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(CONFIG_RETRIEVE_C2S, ConfigRetrieveC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(CONFIG_SYNC_S2C, ConfigSyncS2CPacket::receive);
    }

    public static class ConfigRetrieveC2SPacket {
        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                                       PacketByteBuf buf, PacketSender responseSender) {
            server.getRegistryManager().get(ConfigOption.CONFIG.getKey()).forEach(config -> {
                config.markDirty();
                config.sync(server);
            });

        }
    }

    public static class ConfigSyncC2SPacket {
        public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                                                       PacketByteBuf buf, PacketSender responseSender) {
            ConfigOption<?> config = ConfigOption.getConfig(buf.readIdentifier());
            if (config instanceof IntegerConfigOption intConfig) {
                intConfig.setValue(buf.readInt());
            } else if (config instanceof BooleanConfigOption boolConfig) {
                boolConfig.setValue(buf.readBoolean());
            } else if (config instanceof EnumConfigOption<?> enumConfig) {
                enumConfig.setValue(buf, buf.readEnumConstant(enumConfig.getEnumClass()));
            }
        }
    }

    public static class ConfigSyncS2CPacket{
        public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
            ConfigOption<?> config = ConfigOption.getConfig(buf.readIdentifier());
            if (config instanceof IntegerConfigOption intConfig) {
                intConfig.setValue(buf.readInt());
            } else if (config instanceof BooleanConfigOption boolConfig) {
                boolConfig.setValue(buf.readBoolean());
            } else if (config instanceof EnumConfigOption<?> enumConfig) {
                enumConfig.setValue(buf, buf.readEnumConstant(enumConfig.getEnumClass()));
            }
        }
    }

}
