package illyena.gilding.mixin.client.gui.screen;

import illyena.gilding.config.gui.ModdedWorldGenScreen;
import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static illyena.gilding.GildingInit.translationKeyOf;

@Mixin(CreateWorldScreen.MoreTab.class)
public class MoreTabMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onInit(CreateWorldScreen createWorldScreen, CallbackInfo ci, GridWidget.Adder adder) {
        Text MODDED_WORLD_GEN_BUTTON_TEXT = translationKeyOf("tooltip", "modded_world_gen.button");
        ButtonWidget MODDED_WORLD_GEN_BUTTON = ModdedWorldGenButton.builder(MODDED_WORLD_GEN_BUTTON_TEXT,
                button -> MinecraftClient.getInstance().setScreen(new ModdedWorldGenScreen(MinecraftClient.getInstance().currentScreen)))
                        .width(210).build();

        adder.add(MODDED_WORLD_GEN_BUTTON);
    }
}
