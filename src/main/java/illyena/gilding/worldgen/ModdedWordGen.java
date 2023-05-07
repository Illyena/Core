package illyena.gilding.worldgen;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public class ModdedWordGen {
    public static StructurePlacementType<ConfigurableRandomStructurePlacement> CONFIGURABLE_RANDOM_SPREAD;
    public static StructurePlacementType<ConfigurableConcentricStructurePlacement> CONFIGURABLE_CONCENTRIC_RINGS;

    public static void registerWorldGen( ) {
        CONFIGURABLE_RANDOM_SPREAD = Registry.register(Registry.STRUCTURE_PLACEMENT, "configurable_random_spread", () -> ConfigurableRandomStructurePlacement.CODEC);
        CONFIGURABLE_CONCENTRIC_RINGS = Registry.register(Registry.STRUCTURE_PLACEMENT, "configurable_concentric_rings", () -> ConfigurableConcentricStructurePlacement.CODEC); //todo transfer

    }
}
