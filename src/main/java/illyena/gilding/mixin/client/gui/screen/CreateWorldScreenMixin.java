package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.config.gui.widget.ModTab;
import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import illyena.gilding.core.config.GildingConfigOptions;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TabNavigationWidget;builder(Lnet/minecraft/client/gui/tab/TabManager;I)Lnet/minecraft/client/gui/widget/TabNavigationWidget$Builder;"))
    private TabNavigationWidget.Builder _tabNavigationWidgetBuilder(TabManager tabManager, int width) {
        if (GildingConfigOptions.MODDED_WORLD_GEN_BUTTON_TYPE.getValue() == ModdedWorldGenButton.Type.TAB) {
            return TabNavigationWidget.builder(tabManager, width).tabs(new ModTab());
        } else return TabNavigationWidget.builder(tabManager, width);

    }

}
