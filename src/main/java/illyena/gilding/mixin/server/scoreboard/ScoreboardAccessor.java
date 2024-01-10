package illyena.gilding.mixin.server.scoreboard;

import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(Scoreboard.class)
public interface ScoreboardAccessor {
    @Invoker
    NbtList callToNbt();

    @Invoker
    void callReadNbt(NbtList list);
}
