package illyena.gilding.mixin.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public interface ScreenAccessor {
    @Accessor
    List<Drawable> getDrawables();

    @Accessor
    List<Element> getChildren();

    @Accessor
    MinecraftClient getClient();
}
