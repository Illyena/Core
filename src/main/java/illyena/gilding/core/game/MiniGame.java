package illyena.gilding.core.game;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;
import java.util.Random;

@SuppressWarnings("unused")
public abstract class MiniGame {
    private final MiniGameType<?> type;
    protected ServerWorld world;
    protected int id;
    protected boolean started;
    public boolean gameActive;
    protected int currentTime;
    protected BlockPos center;
    protected Status status;
    protected Scoreboard scoreboard;
    protected final Random random = new Random();

    public MiniGame(MiniGameType<?> type, int id, ServerWorld world, BlockPos pos) {
        this.type = type;
        this.world = world;
        this.id = id;
        this.gameActive = true;
        this.center = pos;
        this.status = Status.ONGOING;
        this.scoreboard = this.world.getScoreboard();
    }

    public MiniGame(MiniGameType<?> type, ServerWorld world, NbtCompound nbt) {
        this.type = type;
        this.world = world;
        this.id = nbt.getInt("Id");
        this.gameActive = nbt.getBoolean("Active");
        this.started = nbt.getBoolean("Started");
        this.currentTime = nbt.getInt("Ticks");
        this.center = new BlockPos(nbt.getInt("CX"), nbt.getInt("CY"), nbt.getInt("CZ"));
        this.status = Status.fromName(nbt.getString("Status"));
    }

    public MiniGameType<?> getType() { return this.type; }

    public boolean hasStopped() { return this.status == Status.STOPPED; }

    public ServerWorld getWorld() { return this.world; }

    public boolean hasStarted() { return this.started; }

    public void begin() {
        this.gameActive = true;
    }

    public void invalidate() {
        this.gameActive = false;
        this.status = Status.STOPPED;
    }

    public void tick() {
        if (!this.hasStopped()) {
            if (this.status == Status.ONGOING) {
                boolean active = this.gameActive;
                this.gameActive = this.world.isChunkLoaded(this.center);
                if (!this.gameActive) {
                    return;
                }
                ++this.currentTime;
                if (this.currentTime >= 48000L) {
                    this.invalidate();
                    return;
                }
            }
        }
        if (this.currentTime % 200 == 0) {
            this.markDirty();
        }
    }

    protected void markDirty() { ((IServerWorld)this.world).getMiniGameManager().markDirty(); }

    public BlockPos getCenter() { return this.center; }

    public int getGameId() { return this.id; }

    public boolean isActive() { return this.gameActive; }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("Type", (MiniGameType.getId(type)).toString());
        nbt.putInt("Id", this.id);
        nbt.putBoolean("Started", this.started);
        nbt.putBoolean("Active", this.gameActive);
        nbt.putInt("Ticks", this.currentTime);
        nbt.putString("Status", this.status.getName());
        nbt.putInt("CX", this.center.getX());
        nbt.putInt("CY", this.center.getY());
        nbt.putInt("CZ", this.center.getZ());

        return nbt;
    }

    protected enum Status {
        ONGOING,
        STOPPED;

        private static final Status[] VALUES = values();

        Status() { }

        static Status fromName(String name) {
            for (Status status : VALUES) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
            return ONGOING;
        }

        public String getName() { return this.name().toLowerCase(Locale.ROOT); }

    }

}
