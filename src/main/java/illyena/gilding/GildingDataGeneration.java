package illyena.gilding;

import illyena.gilding.core.util.data.GildingBlockTagGenerator;
import illyena.gilding.core.util.data.GildingItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class GildingDataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(GildingBlockTagGenerator::new);
        pack.addProvider(GildingItemTagGenerator::new);
    }

}
