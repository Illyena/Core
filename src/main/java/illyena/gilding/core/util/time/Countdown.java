package illyena.gilding.core.util.time;

import illyena.gilding.core.client.render.ColorAssist;
import illyena.gilding.core.game.MiniGame;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Countdown {
    private static List<Countdown> COUNTDOWNS = new ArrayList<>();
    public boolean active;
    private final ColorAssist colorAssist;
    int totalTicks;
    int ticks;
    List<ServerPlayerEntity> targets;
    Supplier<MiniGame> supplier;

    public Countdown(int totalTicks, @Nullable Supplier<MiniGame> supplier, List<ServerPlayerEntity> targets) {
        this.active = true;
        this.totalTicks = totalTicks * 60;
        this.ticks = this.totalTicks;
        this.targets = targets;
        this.supplier = supplier;
        this.colorAssist = new ColorAssist(this.totalTicks, Color.RED, Color.YELLOW, Color.GREEN);

        TitleFadeS2CPacket titleFadeS2CPacket = new TitleFadeS2CPacket(5, 15, 5);
        targets.forEach((serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(titleFadeS2CPacket)));
        COUNTDOWNS.add(this);
    }

    public Countdown(int totalTicks, @Nullable Supplier<MiniGame> supplier, ServerWorld world) {
        this(totalTicks, supplier, world.getPlayers());
    }

    public void tick() {
        if (this.ticks > 0 && this.ticks % 60 == 0) {
           this.targets.forEach(serverPlayerEntity -> serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText(String.valueOf(ticks / 60))
                   .setStyle(Style.EMPTY.withColor(this.colorAssist.getChromaticFadeColorInt(this.totalTicks - this.ticks, 1.0f))
           ))));
        }

        this.ticks--;
        if (ticks == 0) {
            this.targets.forEach(serverPlayerEntity ->
                    serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(new LiteralText("Go!").setStyle(Style.EMPTY.withColor(Color.GREEN.getRGB())))));

            if (supplier != null) {
                supplier.get();
            }
            resetCountdown();
        }
    }

    private void resetCountdown() {
        this.targets.forEach((serverPlayerEntity) -> new ClearTitleS2CPacket(true));
        this.active = false;
    }

    public static List<Countdown> getCountdowns() { return COUNTDOWNS; }

}
