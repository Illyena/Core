package illyena.gilding.mixin.client.gui.screen;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {
    @Accessor
    boolean isMoreOptionsOpen();

    @Invoker
    void callSetMoreOptionsOpen(boolean moreOptionsOpen);
}
