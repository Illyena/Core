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
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

import static illyena.gilding.worldgen.ModdedWorldGen.CONFIGURABLE_RANDOM_SPREAD;

public class ConfigurableRandomStructurePlacement extends RandomSpreadStructurePlacement {
    IntegerConfigOption spacingConfig;
    IntegerConfigOption separationConfig;

    public static final Codec<ConfigurableRandomStructurePlacement> CODEC = (Codec<ConfigurableRandomStructurePlacement>) RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ConfigOption.CONFIG.getCodec().fieldOf("spacing_config").forGetter(placement -> ((ConfigurableRandomStructurePlacement) placement).spacingConfig),
                    ConfigOption.CONFIG.getCodec().fieldOf("separation_config").forGetter(placement -> ((ConfigurableRandomStructurePlacement) placement).separationConfig),
                    SpreadType.CODEC.optionalFieldOf("spreadType", SpreadType.LINEAR).forGetter(placement -> ((ConfigurableRandomStructurePlacement)placement).spreadType()),
                    Codecs.NONNEGATIVE_INT.fieldOf("salt").forGetter(placement -> ((ConfigurableRandomStructurePlacement)placement).salt()),
                    Vec3i.createOffsetCodec(16).fieldOf("locate_offset").forGetter(placement -> ((ConfigurableRandomStructurePlacement)placement).locateOffset()))
            .apply(instance, ConfigurableRandomStructurePlacement::new)).flatXmap(placement -> ((ConfigurableRandomStructurePlacement)placement).validate((ConfigurableRandomStructurePlacement) placement), DataResult::success).codec();

    public ConfigurableRandomStructurePlacement(ConfigOption<Integer> spacing, ConfigOption<Integer> separation, SpreadType  spreadType, int salt, Vec3i offset) {
        super(spacing.getValue(), separation.getValue(), spreadType, salt);
        this.spacingConfig = (IntegerConfigOption) spacing;
        this.separationConfig = (IntegerConfigOption) separation;
    }

    public StructurePlacementType<?> getType() { return CONFIGURABLE_RANDOM_SPREAD; }

    public int getSpacing() { return this.spacingConfig.getValue(); }

    public int getSeparation() { return this.separationConfig.getValue(); }

    private DataResult<?> validate(ConfigurableRandomStructurePlacement placement) {
        return placement.getSpacing() <= placement.getSeparation() ? DataResult.error("Spacing has to be larger than separation") : DataResult.success(placement);
    }

}
