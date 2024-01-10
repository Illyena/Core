package illyena.gilding.core.game;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MiniGameManager extends PersistentState {
    public final  Map<Integer, MiniGame> games = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;

    public MiniGameManager(ServerWorld world){
        this.world = world;
        this.nextAvailableId = 1;
        this.markDirty();
    }

    public MiniGame getGameById(int id) { return this.games.get(id);}

    public void tick() {
        Iterator<MiniGame> iterator = this.games.values().iterator();
        while (iterator.hasNext()) {
            MiniGame game = iterator.next();
            if (game.hasStopped()) {
                iterator.remove();
                this.markDirty();
            } else {
                game.tick();
            }
        }
    }

    @Nullable
    public MiniGame beginGame(ServerCommandSource source, Supplier<MiniGame> supplier) {
        MiniGame miniGame = this.getOrCreateGame(source.getWorld(), new BlockPos(source.getPosition()), supplier);
        boolean bl = false;
        if (!miniGame.hasStarted()) {
            if (!this.games.containsKey(miniGame.getGameId())) {
                this.games.put(miniGame.getGameId(), miniGame);
            }

            bl = true;
        }
        if (bl) {
            miniGame.begin();
        }
        this.markDirty();
        return miniGame;
    }

    @Nullable
    public MiniGame beginGame(ServerPlayerEntity player, Supplier<MiniGame> supplier) {
        MiniGame miniGame = this.getOrCreateGame(player.getWorld(), player.getBlockPos(), supplier);
        boolean bl = false;
        if (!miniGame.hasStarted()) {
            if (!this.games.containsKey(miniGame.getGameId())) {
                 this.games.put(miniGame.getGameId(), miniGame);
            }

            bl = true;
        }
        if (bl) {
            miniGame.begin();
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, (byte)43));
        }
        this.markDirty();
        return miniGame;
    }

    private MiniGame getOrCreateGame(ServerWorld world, BlockPos pos, Supplier<MiniGame> supplier) {
        MiniGame miniGame = ((IServerWorld)world).getMiniGameManager().getMiniGameAt(pos, 9216);
        return miniGame != null ? miniGame : supplier.get();
    }

    public static <T extends MiniGame> MiniGameManager fromNbt(ServerWorld world, NbtCompound nbtCompound) {
        MiniGameManager manager = new MiniGameManager(world);
        manager.nextAvailableId = nbtCompound.getInt("NextAvailableId");
        NbtList nbtList = nbtCompound.getList("Games", 10);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbt = nbtList.getCompound(i);

            if (nbt.contains("Type")) {
                Optional<MiniGameType<?>> optional = MiniGameType.get(nbt.getString("Type"));
                if (optional.isPresent()) {
                    MiniGameType<?> type = optional.get();
                    T miniGame = (T) type.create(world, nbt);
                    manager.games.put(miniGame.getGameId(), miniGame);
                }
            }
        }
        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("NextAvailableId", this.nextAvailableId);
        NbtList nbtList = new NbtList();
        nbt.put("Games", this.handleGameList(nbtList));
        return nbt;
    }

    private NbtList handleGameList(NbtList nbtList) {
        for (MiniGame game : this.games.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            game.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }
        return nbtList;
    }

    public static String nameFor(RegistryEntry<DimensionType> dimension) {
        if (dimension.matchesKey(DimensionType.THE_NETHER_REGISTRY_KEY)) {
            return "games_nether";
        } else if (dimension.matchesKey(DimensionType.THE_END_REGISTRY_KEY)) {
            return "games_end";
        } else {
            return "games";
        }
    }

    public int nextId() { return ++this.nextAvailableId; }

    @Nullable
    public MiniGame getMiniGameAt(BlockPos pos, int searchDistance) {
        MiniGame game = null;
        double d = searchDistance;
        for (MiniGame miniGame : this.games.values()) {
            double e = miniGame.getCenter().getSquaredDistance(pos);
            if (miniGame.isActive() && e < d) {
                game =  miniGame;
                d = e;
            }
        }
        return game;
    }

}
