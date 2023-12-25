package illyena.gilding.core.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface TeleportCallback {
    public static final Event<TeleportCallback> TELEPORT_EVENT = EventFactory.createArrayBacked(TeleportCallback.class,
            (listeners) -> (world, entity, pos) -> {
        for (TeleportCallback listener : listeners) {
            ActionResult result = listener.teleport(world, entity, pos);
            if (result != ActionResult.PASS) {
                return result;
            }
        }
        return ActionResult.PASS;
            });

    public abstract ActionResult teleport(World world, Entity entity, BlockPos pos);

}
