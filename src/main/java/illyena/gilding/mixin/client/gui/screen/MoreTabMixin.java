package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CreateWorldScreen.MoreTab.class)
public class MoreTabMixin extends GridScreenTab {

    public MoreTabMixin(Text title) { super(title); }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onInit(CreateWorldScreen createWorldScreen, CallbackInfo ci, GridWidget.Adder adder) {
        ModdedWorldGenButton.moreTabConfig(createWorldScreen, adder);
    }

}
