package illyena.gilding.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

import java.util.Optional;

import static illyena.gilding.worldgen.ModdedWorldGen.CONFIGURABLE_RANDOM_SPREAD;

public class ConfigurableRandomStructurePlacement extends RandomSpreadStructurePlacement {
    IntegerConfigOption spacingConfig;
    IntegerConfigOption separationConfig;

    public static final Codec<ConfigurableRandomStructurePlacement> CODEC = (Codec<ConfigurableRandomStructurePlacement>) RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ConfigOption.CONFIG.getCodec().fieldOf("spacing_config").forGetter(placement -> ((ConfigurableRandomStructurePlacement) placement).spacingConfig),
                    ConfigOption.CONFIG.getCodec().fieldOf("separation_config").forGetter(placement -> ((ConfigurableRandomStructurePlacement) placement).separationConfig),
                    SpreadType.CODEC.optionalFieldOf("spreadType", SpreadType.LINEAR).forGetter(placement -> ((ConfigurableRandomStructurePlacement)placement).getSpreadType()),
                    Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(placement -> ((ConfigurableRandomStructurePlacement)placement).getSalt()))
            .apply(instance, ConfigurableRandomStructurePlacement::new)).flatXmap(placement -> ((ConfigurableRandomStructurePlacement)placement).validate((ConfigurableRandomStructurePlacement) placement), DataResult::success).codec();

    public ConfigurableRandomStructurePlacement(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<StructurePlacement.ExclusionZone> exclusionZone, ConfigOption<Integer> spacing, ConfigOption<Integer> separation, SpreadType spreadType) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing.getValue(), separation.getValue(), spreadType);
        this.spacingConfig = (IntegerConfigOption) spacing;
        this.separationConfig = (IntegerConfigOption) separation;
    }

    public ConfigurableRandomStructurePlacement(ConfigOption<Integer> spacing, ConfigOption<Integer> separation, SpreadType  spreadType, int salt) {
        this(Vec3i.ZERO, FrequencyReductionMethod.DEFAULT, 1.0f, salt, Optional.empty(), spacing, separation, spreadType);
    }

    public StructurePlacementType<?> getType() { return CONFIGURABLE_RANDOM_SPREAD; }

    public int getSpacing() { return this.spacingConfig.getValue(); }

    public int getSeparation() { return this.separationConfig.getValue(); }

    private DataResult<?> validate(ConfigurableRandomStructurePlacement placement) {
        return placement.getSpacing() <= placement.getSeparation() ? DataResult.error(() -> "Spacing has to be larger than separation") : DataResult.success(placement);
    }

}
