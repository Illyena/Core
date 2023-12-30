package illyena.gilding.core.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.core.client.gui.screen.GildingMenuScreen;
import illyena.gilding.mixin.client.gui.widget.PressableWidgetAccessor;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.core.config.GildingConfigOptions.*;

public class GildingMenuButton extends ButtonWidget {
    public static final ItemStack ICON = Blocks.GILDED_BLACKSTONE.asItem().getDefaultStack();
    private static final Text GILDING_MENU_BUTTON_TOOLTIP = Text.translatable("tooltip." + SUPER_MOD_ID + ".button").formatted(Formatting.ITALIC);

    public GildingMenuButton(int x, int y, @Nullable Text tooltip) {
        super(x, y, 20, 20, Text.empty(), GildingMenuButton::click, Supplier::get);
        this.setTooltip(Tooltip.of(tooltip));
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, ((PressableWidgetAccessor)this).callGetTextureY());
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawItem(ICON, this.getX() + 2 , this.getY() + 2);
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new GildingMenuScreen(MinecraftClient.getInstance().currentScreen)));
    }

    public static class GildingMenuButtonHandler {
        public static void afterScreenInit(MinecraftClient client, Screen screen , int scaledWidth, int i) {
            if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
                int offsetX;
                int gildingButtonIndex;
                List<Integer> buttonYs = buttonYs(screen).stream().sorted().toList();
                if (screen instanceof TitleScreen) {
                    MAIN_MENU_CONFIG_BUTTON_ROW.setMutableMax(buttonYs.size());
                    offsetX = MAIN_MENU_CONFIG_BUTTON_OFFSET.getValue();
                    gildingButtonIndex = Math.min(buttonYs.size(), MAIN_MENU_CONFIG_BUTTON_ROW.getValue()) - 1;
                } else {
                    IN_GAME_MENU_CONFIG_BUTTON_ROW.setMutableMax(buttonYs.size());
                    offsetX = IN_GAME_MENU_CONFIG_BUTTON_OFFSET.getValue();
                    gildingButtonIndex = Math.min(buttonYs.size(), IN_GAME_MENU_CONFIG_BUTTON_ROW.getValue()) - 1;
                }

                boolean onLeft = offsetX < 0;
                if (gildingButtonIndex != -1) {

                    int buttonsY = buttonYs.get(gildingButtonIndex);
                    final ClickableWidget[] reference = {null};
                    Screens.getButtons(screen).forEach(w -> {
                        if (w.getY() == buttonsY) {
                            if (reference[0] == null) {
                                reference[0] = w;
                            } else if (onLeft && reference[0].getX() > w.getX()) {
                                reference[0] = w;
                            } else if (!onLeft && reference[0].getX() < w.getX()) {
                                reference[0] = w;
                            }
                        }
                    });
                    if (reference[0] != null) {
                        Screens.getButtons(screen).add(gildingButtonIndex,
                                new GildingMenuButton(reference[0].getX() + offsetX + (onLeft ? -20 : reference[0].getWidth()), buttonsY, GILDING_MENU_BUTTON_TOOLTIP));
                    }
                }

            }
        }

        public static List<Integer> buttonYs(Screen screen) {
            final List<ClickableWidget> buttons = Screens.getButtons(screen);
            Map<Integer, ClickableWidget> map = new HashMap<>();
            buttons.stream()
                    .filter(w -> !w.getMessage().equals(TitleScreen.COPYRIGHT) && !w.getMessage().equals(Text.translatable("menu.game")))
                    .forEach(w -> map.put(w.getY(), w));
            return map.keySet().stream().distinct().sorted().toList();
        }
    }

}
