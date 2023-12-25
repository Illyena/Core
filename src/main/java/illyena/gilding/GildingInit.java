package illyena.gilding;

import illyena.gilding.compat.Mod;
import illyena.gilding.config.command.ConfigArguments;
import illyena.gilding.config.command.ConfigCommand;
import illyena.gilding.config.network.ConfigNetworking;
import illyena.gilding.core.config.GildingConfigOptions;
import illyena.gilding.core.enchantment.GildingEnchantments;
import illyena.gilding.core.loot.condition.GildingLootConditionTypes;
import illyena.gilding.core.networking.GildingPackets;
import illyena.gilding.core.particle.GildingParticles;
import illyena.gilding.worldgen.ModdedWorldGen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GildingInit implements ModInitializer {
    public static final String SUPER_MOD_ID = "gilding";
    public static final String SUPER_MOD_NAME = "Gilding";
    public static final String VERSION = Mod.getModVersion(SUPER_MOD_ID);

    public static final Logger LOGGER = LogManager.getLogger(SUPER_MOD_NAME);

    public static final Mod GILDING = new Mod(SUPER_MOD_ID, null, true, GildingConfigOptions.class);

    @Override
    public void onInitialize() {
        ConfigNetworking.registerC2SPackets();
        ConfigArguments.registerArgumentTypes();
        ConfigCommand.registerConfigCommand();

        LOGGER.info("Welcome to the {} Mod!", SUPER_MOD_NAME);

        GildingEnchantments.callEnchantments();
        GildingParticles.callGildingParticles();

        GildingPackets.registerC2SPackets();
        ModdedWorldGen.registerWorldGen();
        GildingLootConditionTypes.callLootConditions();
    }

    public static TranslatableText translationKeyOf(String type, String key) {
        return new TranslatableText(type + "." + SUPER_MOD_ID + "." + key);
    }

}
