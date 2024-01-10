package illyena.gilding.core.game;

import com.mojang.serialization.Lifecycle;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

@SuppressWarnings("unused")
public class MiniGameType<T extends MiniGame> implements TypeFilter<MiniGame, T> {
    private static final RegistryKey<Registry<MiniGameType<?>>> key = RegistryKey.ofRegistry(new Identifier(SUPER_MOD_ID, "mini_game_type_key"));
    public static final Registry<MiniGameType<?>> MINI_GAME_TYPES = new SimpleRegistry<>(key, Lifecycle.experimental(), MiniGameType::getRegistryEntry);
    private final RegistryEntry.Reference<MiniGameType<?>> registryEntry;
    private final MiniGameFactory<T> factory;
    private final NbtCompound nbtCompound;

    public static <T extends MiniGame> MiniGameType<?> register(Identifier id, Builder<T> builder) {
        return Registry.register(MINI_GAME_TYPES, id, builder.build());
    }

    public static Identifier getId(MiniGameType<?> type) {
        return MINI_GAME_TYPES.getId(type);
    }

    public static Optional<MiniGameType<?>> get(String id) {
        return MINI_GAME_TYPES.getOrEmpty(Identifier.tryParse(id));
    }

    public MiniGameType(MiniGameFactory<T> factory, NbtCompound nbtCompound) {
        this.registryEntry = MINI_GAME_TYPES.createEntry(this);
        this.factory = factory;
        this.nbtCompound = nbtCompound;
    }

    public T create(ServerWorld world, NbtCompound nbtCompound) {
        return this.factory.create(this, world, nbtCompound);
    }

    @Nullable
    @Override
    public T downcast(MiniGame miniGame) { return miniGame.getType() == this ? (T) miniGame : null; }

    @Override
    public Class<? extends MiniGame> getBaseClass() { return MiniGame.class; }

    public RegistryEntry.Reference<MiniGameType<?>> getRegistryEntry() { return this.registryEntry; }

    public static class Builder<T extends MiniGame> {
       private final NbtCompound nbtCompound;
       private final MiniGameFactory<T> factory;

       protected Builder(NbtCompound nbtCompound, MiniGameType.MiniGameFactory<T> factory) {
           this.nbtCompound = nbtCompound;
           this.factory = factory;
       }

       public static <T extends MiniGame> Builder<T> create(NbtCompound nbtCompound, MiniGameFactory<T> factory) {
           return new Builder<>(nbtCompound, factory);
       }

       public MiniGameType<T> build() { return new MiniGameType<>(this.factory, this.nbtCompound); }
    }

    public interface MiniGameFactory<T extends MiniGame> {

        T create(MiniGameType<T> type, ServerWorld world, NbtCompound nbtCompound);

    }

}
