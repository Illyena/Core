package illyena.gilding;

import illyena.gilding.compat.Mod;
import illyena.gilding.config.network.ConfigNetworking;
import illyena.gilding.core.client.gui.screen.GildingConfigMenu;
import illyena.gilding.core.event.ClientEvents;
import illyena.gilding.core.event.KeyInputHandler;
import illyena.gilding.core.item.IUnbreakable;
import illyena.gilding.core.networking.GildingPackets;
import illyena.gilding.core.particle.GildingParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

@Environment(EnvType.CLIENT)
public class GildingClientInit implements ClientModInitializer {
    public static final Screen GILDING_CONFIG_SCREEN = Mod.ModScreens.registerConfigScreen(SUPER_MOD_ID, new GildingConfigMenu());

    @Override
    public void onInitializeClient() {
        ConfigNetworking.registerS2CPackets();
        GildingPackets.registerS2CPackets();
        KeyInputHandler.register();
        ClientEvents.registerClientEvents();

        GildingParticles.registerParticles();

    }

    public static final UnclampedModelPredicateProvider BROKEN = ModelPredicateProviderRegistry.register(new Identifier("broken"),
            (stack, world, entity, seed) -> stack.getItem() instanceof IUnbreakable item && item.isUsable(stack) ? 0.0f : 1.0f);


}
