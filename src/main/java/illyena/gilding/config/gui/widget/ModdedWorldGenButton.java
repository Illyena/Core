package illyena.gilding.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ModdedWorldGenScreen;
import illyena.gilding.mixin.client.gui.screen.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.*;

public class ModdedWorldGenButton extends ButtonWidget {
    private boolean small;
    public static final ItemStack ICON = Items.FILLED_MAP.getDefaultStack();

    public ModdedWorldGenButton(int x, int y, @Nullable Text tooltip, boolean small) {
        super(x, y, small ? 20 : 150, 20, small ? Text.empty() : translationKeyOf("menu", "modded_world_gen.button"),
                ModdedWorldGenButton::click, DEFAULT_NARRATION_SUPPLIER);
        this.setTooltip(Tooltip.of(tooltip));
        this.small = small;
    }

    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.small) {
            context.drawItem(ICON, this.getX() + 2, this.getY() + 2);
        }
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);

    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new ModdedWorldGenScreen(MinecraftClient.getInstance().currentScreen)));
    }

    public static class ModdedWorldGenButtonHandler {
        private static ModdedWorldGenButton MODDED_WORLD_GEN_BUTTON;

        public static void onGuiInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
            if (screen instanceof CreateWorldScreen cwScreen) {
                Text MODDED_WORLD_GEN_BUTTON_TEXT = translationKeyOf("tooltip", "modded_world_gen.button");

                int rowIdx = MODDED_WORLD_GEN_BUTTON_ROW.getValue();
                int offsetX = MODDED_WORLD_GEN_BUTTON_OFFSET.getValue();
                boolean left = offsetX < 0;

                screen.children().stream().filter(c -> c instanceof TabNavigationWidget)
                        .map(c -> (TabNavigationWidget) c)
                        .distinct().collect(ArrayList::new, (list, w) -> list.addAll(w.children()), ArrayList::addAll)
                        .stream().map(b -> (TabButtonWidget) b);

                List<Map.Entry<Integer, Pair<ClickableWidget, ClickableWidget>>> widgets = ((ScreenAccessor) screen).getChildren().stream()
                        .filter(w -> w instanceof ClickableWidget)
                        .map(w -> (ClickableWidget) w)
                        .filter(w -> w.visible)
                        .collect(HashMap<Integer, Pair<ClickableWidget, ClickableWidget>>::new, (map, w) -> {
//                            ClickableWidget hidden = new ButtonWidget(screen.width / 2 - 155, 122, 150, 0, Text.empty(), button -> {});
                            ClickableWidget hidden = ButtonWidget.builder(Text.empty(), button -> {
                            }).dimensions(screen.width / 2 - 155, 122, 0, 0).build();
                            if (!map.containsKey(hidden.getY())) {
                                map.put(hidden.getY(), new Pair<>(hidden, null));
                            }
                            boolean right = w.getX() >= screen.width / 2;
                            if (!map.containsKey(w.getY())) {
                                Pair<ClickableWidget, ClickableWidget> pair = right ? new Pair<>(null, w) : new Pair<>(w, null);
                                map.put(w.getY(), pair);
                            } else {
                                Pair<ClickableWidget, ClickableWidget> pair2 = map.get(w.getY());
                                if (right ? pair2.getRight() == null || pair2.getRight().getX() < w.getX() : pair2.getLeft() == null || pair2.getLeft().getX() > w.getX()) {
                                    if (right) {
                                        pair2.setRight(w);
                                    } else {
                                        pair2.setLeft(w);
                                    }
                                    map.put(w.getY(), pair2);
                                }
                            }
                        }, HashMap::putAll)
                        .entrySet().stream().sorted(Map.Entry.comparingByKey()).toList();

                Pair<ClickableWidget, ClickableWidget> pair = widgets.get(Math.min(rowIdx, widgets.size())).getValue();

                ClickableWidget reference = left ? pair.getLeft() : pair.getRight();
                boolean bl = reference == null;
                if (bl) {
                    reference = left ? pair.getRight() : pair.getLeft();
                }

                int x = screen.width / 2 + (left ? -1 : 1) * (bl ? 0 : reference.getWidth()) + offsetX;
                x = x + (left ? MODDED_WORLD_GEN_BUTTON_SIZE.getValue() ? -20 : -150 : 0);

                MODDED_WORLD_GEN_BUTTON = new ModdedWorldGenButton(x, reference.getY(), MODDED_WORLD_GEN_BUTTON_TEXT, MODDED_WORLD_GEN_BUTTON_SIZE.getValue());

                //            screen.addDrawableChild(MODDED_WORLD_GEN_BUTTON);
                //             MODDED_WORLD_GEN_BUTTON.visible = ((CreateWorldScreenAccessor) screen).isMoreOptionsOpen();
            }
        }

        public static void setVisible(boolean moreOptionsOpen) {
            if (MODDED_WORLD_GEN_BUTTON != null) {
                MODDED_WORLD_GEN_BUTTON.visible = moreOptionsOpen;
            }
        }
    }

    private class ModdedTab extends GridScreenTab {
        private static final Text MODDED_TAB_TITLE = Text.translatable("menu." + SUPER_MOD_ID + ".tab");

        public ModdedTab() {
            super(MODDED_TAB_TITLE);
            GridWidget.Adder adder = this.grid.setRowSpacing(8).createAdder(1);
            Positioner positioner = adder.copyPositioner();

        }
    }
}
