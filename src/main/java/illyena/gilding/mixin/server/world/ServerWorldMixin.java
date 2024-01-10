package illyena.gilding.mixin.server.world;

import illyena.gilding.core.game.IServerWorld;
import illyena.gilding.core.game.MiniGameManager;
import illyena.gilding.core.util.time.Countdown;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements IServerWorld{
    @Unique @Mutable @Final protected MiniGameManager miniGameManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, RegistryEntry<DimensionType> registryEntry, WorldGenerationProgressListener worldGenerationProgressListener, ChunkGenerator chunkGenerator, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {
        ServerWorld world = (ServerWorld)(Object)this;
        this.miniGameManager = world.getPersistentStateManager().getOrCreate(nbtCompound ->
                MiniGameManager.fromNbt(world, nbtCompound),
                () -> new MiniGameManager(world),
                MiniGameManager.nameFor(world.method_40134()));
    }


    @Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Profiler profiler) {
        Iterator<Countdown> iterator = Countdown.getCountdowns().listIterator();
        while (iterator.hasNext()) {
            Countdown countdown = iterator.next();
            if (!countdown.active) {
                iterator.remove();
            } else {
                countdown.tick();
            }
        }

        if (this.miniGameManager != null) {
            profiler.push("game");
            this.miniGameManager.tick();
            profiler.pop();
        }
    }

    public MiniGameManager getMiniGameManager() { return miniGameManager; }

}
