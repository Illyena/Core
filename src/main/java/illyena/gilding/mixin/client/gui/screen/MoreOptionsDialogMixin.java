package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MoreOptionsDialog.class)
public class MoreOptionsDialogMixin {
    @Inject(method = "setVisible", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;bonusItemsButton:Lnet/minecraft/client/gui/widget/CyclingButtonWidget;", ordinal = 1))
    private void onSetVisible(boolean visible, CallbackInfo ci) {
        ModdedWorldGenButton.ModdedWorldGenButtonHandler.setVisible(visible);
    }
}
