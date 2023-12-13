package illyena.gilding.config.gui.widget;

import illyena.gilding.config.gui.ModdedWorldGenScreen;
import illyena.gilding.config.option.util.HasTooltip;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.MODDED_WORLD_GEN_BUTTON_ROW;
import static illyena.gilding.core.config.GildingConfigOptions.MODDED_WORLD_GEN_BUTTON_TYPE;

public class ModdedWorldGenButton {
    public static final Text MODDED_WORLD_GEN_TEXT = translationKeyOf("menu", "modded_world_gen.button");
    public static final Tooltip MODDED_WORLD_GEN_TOOLTIP = Tooltip.of(translationKeyOf("tooltip", "modded_world_gen.button"));
    public static final Identifier ICON_TEXTURE = new Identifier("minecraft", "textures/item/filled_map.png");

    public static void moreTabConfig(CreateWorldScreen createWorldScreen, GridWidget.Adder adder) {
        if (MODDED_WORLD_GEN_BUTTON_TYPE.getValue() == Type.BUTTON) {
            adder.add(ButtonWidget.builder(MODDED_WORLD_GEN_TEXT, onPress(createWorldScreen))
                    .width(210).tooltip(MODDED_WORLD_GEN_TOOLTIP).build());
        } else if (MODDED_WORLD_GEN_BUTTON_TYPE.getValue() == Type.ICON) {
            GridWidget gridWidget = adder.getGridWidget();
            gridWidget.setColumnSpacing(4);
            gridWidget.add(new TexturedButtonWidget(gridWidget.getX(), gridWidget.getY(), 20, 20,
                                    0, 0, 20, ICON_TEXTURE, 20, 20,
                                    onPress(createWorldScreen)), Math.max(MODDED_WORLD_GEN_BUTTON_ROW.getValue() - 1, 0), 1)
                    .setTooltip(Tooltip.of(MODDED_WORLD_GEN_TEXT));
        }
    }

    public static ButtonWidget.PressAction onPress(Screen screen) {
        return button -> Screens.getClient(screen).setScreen(new ModdedWorldGenScreen(screen));
    }

    public enum Type implements HasTooltip {
        ICON,
        BUTTON,
        TAB;

        Type () {  }

        public Tooltip getTooltip() {
            return Tooltip.of(translationKeyOf("tooltip", "mwg_button_config." + this.name().toLowerCase()));
        }
    }
}
