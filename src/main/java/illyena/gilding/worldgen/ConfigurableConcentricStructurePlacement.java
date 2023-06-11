package illyena.gilding.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

import java.util.List;

import static illyena.gilding.worldgen.ModdedWorldGen.CONFIGURABLE_CONCENTRIC_RINGS;

public class ConfigurableConcentricStructurePlacement extends ConcentricRingsStructurePlacement {
    int distance;
    IntegerConfigOption spreadConfig;
    IntegerConfigOption countConfig;

    public static final Codec<ConfigurableConcentricStructurePlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConfigurableConcentricStructurePlacement::distance),
                    ConfigOption.CONFIG.getCodec().fieldOf("spread_config").forGetter(placement -> placement.spreadConfig),
                    ConfigOption.CONFIG.getCodec().fieldOf("count_config").forGetter(placement -> placement.countConfig))
            .apply(instance, ConfigurableConcentricStructurePlacement::new));

    public ConfigurableConcentricStructurePlacement(int distance, ConfigOption<Integer> spread, ConfigOption<Integer> count) {
        super(distance, spread.getValue(), count.getValue());
        this.distance = distance;
        this.spreadConfig = (IntegerConfigOption)spread;
        this.countConfig = (IntegerConfigOption)count;
    }

    @Override
    public boolean isStartChunk(ChunkGenerator chunkGenerator, long seed, int chunkX, int chunkZ) {
        List<ChunkPos> list = chunkGenerator.getConcentricRingsStartChunks(this);
        return list != null && list.contains(new ChunkPos(chunkX, chunkZ));
    }

    @Override
    public StructurePlacementType<?> getType() { return CONFIGURABLE_CONCENTRIC_RINGS; }

    public int distance() { return this.distance; }

    public int spread() { return this.spreadConfig.getValue(); }

    public int count() { return this.countConfig.getValue(); }
}
