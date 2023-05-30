package illyena.gilding.config.gui.widget;

import illyena.gilding.config.gui.ModdedWorldGenScreen;
import illyena.gilding.core.client.gui.widget.GildingMenuButton;
import illyena.gilding.mixin.client.gui.screen.CreateWorldScreenAccessor;
import illyena.gilding.mixin.client.gui.screen.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.*;

public class ModdedWorldGenButton extends ButtonWidget {
    public ModdedWorldGenButton(int x, int y, @Nullable TooltipSupplier tooltip, boolean small) {
        super(x, y, small ? 20 : 150, 20, small ? Text.empty() : translationKeyOf("menu", "modded_world_gen.button"), ModdedWorldGenButton::click, tooltip);
    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new ModdedWorldGenScreen(MinecraftClient.getInstance().currentScreen)));
    }

    public static class MenuRows {
        public static final MenuRows MORE_OPTIONS = new MenuRows(Arrays.asList(

                new GildingMenuButton.SingleMenuRow("selectWorld.mapFeatures", "selectWorld.mapType"),
                new GildingMenuButton.SingleMenuRow("selectWorld.bonusItems", null),
                new GildingMenuButton.SingleMenuRow("selectWorld.import_world_gen_settings", "gui.done"),
                new GildingMenuButton.SingleMenuRow("selectWorld.create", "gui.cancel")
        ));

        protected final List<String> leftButtons, rightButtons;

        public MenuRows(List<GildingMenuButton.SingleMenuRow> variants) {
            leftButtons = variants.stream().map(r -> r.left).collect(Collectors.toList());
            rightButtons = variants.stream().map(r -> r.right).collect(Collectors.toList());
        }
    }

    public static class ModdedWorldGenButtonHandler {
        private static ModdedWorldGenButton MODDED_WORLD_GEN_BUTTON;

        public static void onGuiInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
            if (screen instanceof CreateWorldScreen) {
                TooltipSupplier tooltipSupplier = new TooltipSupplier() {
                    private final Text MODDED_WORLD_GEN_BUTTON_TEXT = translationKeyOf("tooltip", "modded_world_gen.button");

                    @Override
                    public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                        if (button.active) {
                            client.currentScreen.renderTooltip(matrices, this.MODDED_WORLD_GEN_BUTTON_TEXT, mouseX, mouseY);
                        }
                    }

                    public void supply(Consumer<Text> consumer) {
                        consumer.accept(this.MODDED_WORLD_GEN_BUTTON_TEXT);
                    }
                };

                if (MODDED_WORLD_GEN_BUTTON_SIZE.getValue()) {
                    MenuRows menu = MenuRows.MORE_OPTIONS;
                    int rowIdx = MODDED_WORLD_GEN_BUTTON_ROW.getValue();
                    int offsetX = MODDED_WORLD_GEN_BUTTON_OFFSET.getValue();
                    boolean left = offsetX < 0;
                    String target = (left ? menu.leftButtons : menu.rightButtons).get(rowIdx - 1);
                    ((ScreenAccessor) screen).getChildren().stream()
                            .filter(w -> w instanceof ClickableWidget)
                            .map(w -> (ClickableWidget) w)
                            .filter(w -> w.getMessage() instanceof MutableText t && t.getContent() instanceof TranslatableTextContent content && target.equals(content.getKey()))
                            .findFirst()
                            .ifPresent(w ->
                                    MODDED_WORLD_GEN_BUTTON = new ModdedWorldGenButton(w.x = offsetX + (left ? -20 : w.getWidth()), w.y, tooltipSupplier, true));
                } else {
                    MODDED_WORLD_GEN_BUTTON = new ModdedWorldGenButton(screen.width / 2 + 5, 151, tooltipSupplier, false);
                }
                screen.addDrawableChild(MODDED_WORLD_GEN_BUTTON);
                MODDED_WORLD_GEN_BUTTON.visible = ((CreateWorldScreenAccessor)screen).isMoreOptionsOpen();
            }
        }

        public static void setVisible(boolean moreOptionsOpen) {
            if (MODDED_WORLD_GEN_BUTTON != null) {
                MODDED_WORLD_GEN_BUTTON.visible = moreOptionsOpen;
            }
        }
    }

}
