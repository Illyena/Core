package illyena.gilding.core.client.gui.widget;

import illyena.gilding.core.client.gui.screen.GildingMenuScreen;
import illyena.gilding.mixin.client.gui.screen.CreateWorldScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static illyena.gilding.GildingInit.translationKeyOf;

public class ModdedWorldGenButton extends ButtonWidget {
    public ModdedWorldGenButton(int x, int y, @Nullable TooltipSupplier tooltip) {
        super(x, y, 150, 20, translationKeyOf("menu", "modded_world_gen.button"), ModdedWorldGenButton::click, tooltip);
    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new GildingMenuScreen(MinecraftClient.getInstance().currentScreen)));
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
                 MODDED_WORLD_GEN_BUTTON = new ModdedWorldGenButton(screen.width / 2 + 5, 151, tooltipSupplier);

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

/*
    public static class ModdedWorldGenButtonHandler {
        public static void onGuiInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
            MenuRows rows = null;
            int rowId = 0, offset = 0;

            if (screen instanceof CreateWorldScreen createWorldScreen ) {//&& ((CreateWorldScreenAccessor)createWorldScreen).isMoreOptionsOpen()) {
                rows = MenuRows.MORE_OPTIONS;
                rowId = 2;
                offset = 0;
            }

            if (rowId != 0 && rows != null) {
                boolean onLeft = offset < 0;

     //           String target = (onLeft ? rows.leftButtons : rows.rightButtons).get(rowId -1);

                String target = getTarget(onLeft, rows, rowId) != null ? getTarget(onLeft, rows, rowId) : getTarget(!onLeft, rows, rowId);

                int offsetX = offset;
                GildingInit.LOGGER.error("children {}", ((ScreenAccessor)screen).getChildren().stream());

                ((ScreenAccessor)screen).getChildren().stream()
                        .filter(w -> w instanceof ClickableWidget)
                        .map(w -> (ClickableWidget)w)
                        .filter(w -> w.getMessage() instanceof TranslatableText t && target.equals(t.getKey()))
                        .findFirst()
                        .ifPresent(w -> {
                            TooltipSupplier tooltipSupplier = new TooltipSupplier() {
                                private final Text MODDED_WORLD_GEN_BUTTON_TEXT = translationKeyOf("tooltip", "modded_world_gen.button");

                                @Override
                                public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                                    if (button.active) {
                                        client.currentScreen.renderTooltip(matrices, this.MODDED_WORLD_GEN_BUTTON_TEXT, mouseX, mouseY);
                                    }
                                }
                                public void supply(Consumer<Text> consumer) { consumer.accept(this.MODDED_WORLD_GEN_BUTTON_TEXT); }
                            };

                            screen.addDrawableChild(
                                    new ModdedWorldGenButton(w.x + offsetX + (onLeft ? -150 : w.getWidth()), w.y, tooltipSupplier)
                            );
                        });
            }

        }
    }

    private static String getTarget(boolean onLeft, MenuRows rows, int rowId) {
        return (onLeft ? rows.leftButtons : rows.rightButtons).get(rowId -1);
    }

*/
}
