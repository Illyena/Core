package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.config.gui.widget.ModTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Places new Modded Generation Tab at end of tab list */
@Mixin(TabNavigationWidget.Builder.class)
public class TabNavigationWidgetMixin {
    @Shadow @Final private List<Tab> tabs;

    @Inject(method = "build", at = @At("HEAD"))
    private void onBuild(CallbackInfoReturnable<TabNavigationWidget> cir) {
        if (this.tabs.get(0) instanceof ModTab tab) {
            this.tabs.remove(tab);
            this.tabs.add(this.tabs.size(), tab);
        }
    }

}
