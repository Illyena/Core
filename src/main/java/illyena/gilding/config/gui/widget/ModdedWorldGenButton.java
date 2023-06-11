package illyena.gilding.config.gui.widget;

import illyena.gilding.config.gui.ModdedWorldGenScreen;
import illyena.gilding.mixin.client.gui.screen.CreateWorldScreenAccessor;
import illyena.gilding.mixin.client.gui.screen.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.*;

public class ModdedWorldGenButton extends ButtonWidget {
    private boolean small;
    public static final ItemStack ICON = Items.FILLED_MAP.getDefaultStack();

    public ModdedWorldGenButton(int x, int y, @Nullable TooltipSupplier tooltip, boolean small) {
        super(x, y, small ? 20 : 150, 20, small ? Text.empty() : translationKeyOf("menu", "modded_world_gen.button"), ModdedWorldGenButton::click, tooltip);
        this.small = small;
    }

    @Override
    public void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        if (this.small) {
            MinecraftClient.getInstance().getItemRenderer().renderGuiItemIcon(ICON, x + 2, y +2);
        }
    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new ModdedWorldGenScreen(MinecraftClient.getInstance().currentScreen)));
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

                int rowIdx = MODDED_WORLD_GEN_BUTTON_ROW.getValue();
                int offsetX = MODDED_WORLD_GEN_BUTTON_OFFSET.getValue();
                boolean left = offsetX < 0;

                boolean moreOptions = ((CreateWorldScreenAccessor) screen).isMoreOptionsOpen();
                ((CreateWorldScreenAccessor) screen).callSetMoreOptionsOpen(true);
                List<Map.Entry<Integer, Pair<ClickableWidget, ClickableWidget>>> widgets = ((ScreenAccessor) screen).getChildren().stream()
                        .filter(w -> w instanceof ClickableWidget)
                        .map(w -> (ClickableWidget) w)
                        .filter(w -> w.visible)
                        .collect(HashMap<Integer, Pair<ClickableWidget, ClickableWidget>>::new, (map, w) -> {
                            ClickableWidget hidden = new ButtonWidget(screen.width / 2 - 155, 122, 150, 0, Text.empty(), button -> {});
                            if (!map.containsKey(hidden.y)) {
                                map.put(hidden.y, new Pair<>(hidden, null));
                            }
                            boolean right = w.x >= screen.width / 2;
                            if (!map.containsKey(w.y)) {
                                Pair<ClickableWidget, ClickableWidget> pair = right ? new Pair<>(null, w) : new Pair<>(w, null);
                                map.put(w.y, pair);
                            } else {
                                Pair<ClickableWidget, ClickableWidget> pair2 = map.get(w.y);
                                if (right ? pair2.getRight() == null || pair2.getRight().x < w.x : pair2.getLeft() == null || pair2.getLeft().x > w.x) {
                                    if (right) {
                                        pair2.setRight(w);
                                    } else {
                                        pair2.setLeft(w);
                                    }
                                    map.put(w.y, pair2);
                                }
                            }
                        }, HashMap::putAll)
                        .entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();
                ((CreateWorldScreenAccessor) screen).callSetMoreOptionsOpen(moreOptions);
                Pair<ClickableWidget, ClickableWidget> pair = widgets.get(Math.min(rowIdx, widgets.size())).getValue();

                ClickableWidget reference = left ? pair.getLeft() : pair.getRight();
                boolean bl = reference == null;
                if (bl) {
                    reference = left ? pair.getRight() : pair.getLeft();
                }

                int x = screen.width / 2 + (left ? -1 : 1) * (bl ? 0 : reference.getWidth()) + offsetX;
                x = x + (left ? MODDED_WORLD_GEN_BUTTON_SIZE.getValue() ? -20 : -150 : 0);

                MODDED_WORLD_GEN_BUTTON = new ModdedWorldGenButton(x, reference.y, tooltipSupplier, MODDED_WORLD_GEN_BUTTON_SIZE.getValue());

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
