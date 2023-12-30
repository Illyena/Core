package illyena.gilding.core.util.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class GildingItemTagGenerator extends FabricTagProvider<Item> {
    public static final TagKey<Item> SHIELDS = TagKey.of(Registries.ITEM.getKey(), new Identifier(SUPER_MOD_ID, "shields"));

    public GildingItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(SHIELDS);

//        getOrCreateTagBuilder(ItemTags.WOOL);
//        getOrCreateTagBuilder(ItemTags.PLANKS);
//        getOrCreateTagBuilder(ItemTags.STONE_BRICKS);
//        getOrCreateTagBuilder(ItemTags.WOODEN_BUTTONS);
//        getOrCreateTagBuilder(ItemTags.STONE_BUTTONS);
//        getOrCreateTagBuilder(ItemTags.BUTTONS);
//        getOrCreateTagBuilder(ItemTags.WOOL_CARPETS);
//        getOrCreateTagBuilder(ItemTags.WOODEN_DOORS);
//        getOrCreateTagBuilder(ItemTags.WOODEN_STAIRS);
//        getOrCreateTagBuilder(ItemTags.WOODEN_SLABS);
//        getOrCreateTagBuilder(ItemTags.WOODEN_FENCES);
//        getOrCreateTagBuilder(ItemTags.FENCE_GATES);
//        getOrCreateTagBuilder(ItemTags.WOODEN_PRESSURE_PLATES);
//        getOrCreateTagBuilder(ItemTags.WOODEN_TRAPDOORS);
//        getOrCreateTagBuilder(ItemTags.DOORS);
//        getOrCreateTagBuilder(ItemTags.SAPLINGS);
//        getOrCreateTagBuilder(ItemTags.LOGS_THAT_BURN);
//        getOrCreateTagBuilder(ItemTags.LOGS);
//        getOrCreateTagBuilder(ItemTags.DARK_OAK_LOGS);
//        getOrCreateTagBuilder(ItemTags.OAK_LOGS);
//        getOrCreateTagBuilder(ItemTags.BIRCH_LOGS);
//        getOrCreateTagBuilder(ItemTags.ACACIA_LOGS);
//        getOrCreateTagBuilder(ItemTags.CHERRY_LOGS);
//        getOrCreateTagBuilder(ItemTags.JUNGLE_LOGS);
//        getOrCreateTagBuilder(ItemTags.SPRUCE_LOGS);
//        getOrCreateTagBuilder(ItemTags.MANGROVE_LOGS);
//        getOrCreateTagBuilder(ItemTags.CRIMSON_STEMS);
//        getOrCreateTagBuilder(ItemTags.WARPED_STEMS);
//        getOrCreateTagBuilder(ItemTags.BAMBOO_BLOCKS);
//        getOrCreateTagBuilder(ItemTags.WART_BLOCKS);
//        getOrCreateTagBuilder(ItemTags.BANNERS);
//        getOrCreateTagBuilder(ItemTags.SAND);
//        getOrCreateTagBuilder(ItemTags.SMELTS_TO_GLASS);
//        getOrCreateTagBuilder(ItemTags.STAIRS);
//        getOrCreateTagBuilder(ItemTags.SLABS);
//        getOrCreateTagBuilder(ItemTags.WALLS);
//        getOrCreateTagBuilder(ItemTags.ANVIL);
//        getOrCreateTagBuilder(ItemTags.RAILS);
//        getOrCreateTagBuilder(ItemTags.LEAVES);
//        getOrCreateTagBuilder(ItemTags.TRAPDOORS);
//        getOrCreateTagBuilder(ItemTags.SMALL_FLOWERS);
//        getOrCreateTagBuilder(ItemTags.BEDS);
//        getOrCreateTagBuilder(ItemTags.FENCES);
//        getOrCreateTagBuilder(ItemTags.TALL_FLOWERS);
//        getOrCreateTagBuilder(ItemTags.FLOWERS);
//        getOrCreateTagBuilder(ItemTags.PIGLIN_REPELLENTS);
//        getOrCreateTagBuilder(ItemTags.PIGLIN_LOVED);
//        getOrCreateTagBuilder(ItemTags.IGNORED_BY_PIGLIN_BABIES);
//        getOrCreateTagBuilder(ItemTags.PIGLIN_FOOD);
//        getOrCreateTagBuilder(ItemTags.FOX_FOOD);
//        getOrCreateTagBuilder(ItemTags.GOLD_ORES);
//        getOrCreateTagBuilder(ItemTags.IRON_ORES);
//        getOrCreateTagBuilder(ItemTags.DIAMOND_ORES);
//        getOrCreateTagBuilder(ItemTags.REDSTONE_ORES);
//        getOrCreateTagBuilder(ItemTags.LAPIS_ORES);
//        getOrCreateTagBuilder(ItemTags.COAL_ORES);
//        getOrCreateTagBuilder(ItemTags.EMERALD_ORES);
//        getOrCreateTagBuilder(ItemTags.COPPER_ORES);
//        getOrCreateTagBuilder(ItemTags.NON_FLAMMABLE_WOOD);
//        getOrCreateTagBuilder(ItemTags.SOUL_FIRE_BASE_BLOCKS);
//        getOrCreateTagBuilder(ItemTags.CANDLES);
//        getOrCreateTagBuilder(ItemTags.DIRT);
//        getOrCreateTagBuilder(ItemTags.TERRACOTTA);
//        getOrCreateTagBuilder(ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
//        getOrCreateTagBuilder(ItemTags.BOATS);
//        getOrCreateTagBuilder(ItemTags.CHEST_BOATS);
//        getOrCreateTagBuilder(ItemTags.FISHES);
//        getOrCreateTagBuilder(ItemTags.SIGNS);
//        getOrCreateTagBuilder(ItemTags.MUSIC_DISCS);
//        getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS);
//        getOrCreateTagBuilder(ItemTags.COALS);
//        getOrCreateTagBuilder(ItemTags.ARROWS);
//        getOrCreateTagBuilder(ItemTags.LECTERN_BOOKS);
//        getOrCreateTagBuilder(ItemTags.BOOKSHELF_BOOKS);
//        getOrCreateTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS);
//        getOrCreateTagBuilder(ItemTags.STONE_TOOL_MATERIALS);
//        getOrCreateTagBuilder(ItemTags.STONE_CRAFTING_MATERIALS);
//        getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES);
//        getOrCreateTagBuilder(ItemTags.AXOLOTL_TEMPT_ITEMS);
//        getOrCreateTagBuilder(ItemTags.DAMPENS_VIBRATIONS);
//        getOrCreateTagBuilder(ItemTags.CLUSTER_MAX_HARVESTABLES);
//        getOrCreateTagBuilder(ItemTags.COMPASSES);
//        getOrCreateTagBuilder(ItemTags.HANGING_SIGNS);
//        getOrCreateTagBuilder(ItemTags.CREEPER_IGNITERS);
//        getOrCreateTagBuilder(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS);
//        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR);
//        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS);
//        getOrCreateTagBuilder(ItemTags.TRIM_TEMPLATES);
//        getOrCreateTagBuilder(ItemTags.SNIFFER_FOOD);
//        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS);
//        getOrCreateTagBuilder(ItemTags.DECORATED_POT_INGREDIENTS);
//        getOrCreateTagBuilder(ItemTags.SWORDS);
//        getOrCreateTagBuilder(ItemTags.AXES);
//        getOrCreateTagBuilder(ItemTags.HOES);
//        getOrCreateTagBuilder(ItemTags.PICKAXES);
//        getOrCreateTagBuilder(ItemTags.SHOVELS);
//        getOrCreateTagBuilder(ItemTags.TOOLS);
//        getOrCreateTagBuilder(ItemTags.BREAKS_DECORATED_POTS);
//        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS);
    }

}
