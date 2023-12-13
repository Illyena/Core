package illyena.gilding.worldgen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public class ModdedWorldGen {
    public static StructurePlacementType<ConfigurableRandomStructurePlacement> CONFIGURABLE_RANDOM_SPREAD;
    public static StructurePlacementType<ConfigurableConcentricStructurePlacement> CONFIGURABLE_CONCENTRIC_RINGS;

    public static void registerWorldGen( ) {
        CONFIGURABLE_RANDOM_SPREAD = Registry.register(Registries.STRUCTURE_PLACEMENT, "configurable_random_spread", () -> ConfigurableRandomStructurePlacement.CODEC);
        CONFIGURABLE_CONCENTRIC_RINGS = Registry.register(Registries.STRUCTURE_PLACEMENT, "configurable_concentric_rings", () -> ConfigurableConcentricStructurePlacement.CODEC);
    }

}
