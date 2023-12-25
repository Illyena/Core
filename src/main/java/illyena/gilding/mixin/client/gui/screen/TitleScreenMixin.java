package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.core.client.gui.screen.SharedBackground;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TitleScreen.class)
public class TitleScreenMixin implements SharedBackground {
    @Shadow @Final private RotatingCubeMapRenderer backgroundRenderer;

    @Override
    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
