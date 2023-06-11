package illyena.gilding.core.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.core.client.gui.screen.GildingMenuScreen;
import illyena.gilding.mixin.client.gui.screen.ScreenAccessor;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.core.config.GildingConfigOptions.*;

public class GildingMenuButton extends ButtonWidget {
    public static final ItemStack ICON = Blocks.GILDED_BLACKSTONE.asItem().getDefaultStack();

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
        context.drawNineSlicedTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawItem(ICON, this.getX() + 2 , this.getY() + 2);
        int i = this.active ? 16777215 : 10526880;
        this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public static void click(ButtonWidget button) {
        MinecraftClient.getInstance().send(() ->
                MinecraftClient.getInstance().setScreen(new GildingMenuScreen(MinecraftClient.getInstance().currentScreen)));
    }

    public static class SingleMenuRow {
        public final String left, right;
        public SingleMenuRow(String left, String right) {
            this.left = left;
            this.right = right;
        }
        public SingleMenuRow(String center) {
            this(center, center);
        }
    }

    public static class MenuRows {
        public static final MenuRows MAIN_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.singleplayer"),
                new SingleMenuRow("menu.multiplayer"),
                new SingleMenuRow("menu.online"),
                new SingleMenuRow("narrator.button.language", "narrator.button.accessibility")
        ));

        public static final MenuRows INGAME_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.returnToGame"),
                new SingleMenuRow("gui.advancements", "gui.stats"),
                new SingleMenuRow("menu.sendFeedback", "menu.reportBugs"),
                new SingleMenuRow("menu.options", "menu.shareToLan"),
                new SingleMenuRow("menu.returnToMenu")
        ));

        protected final List<String> leftButtons, rightButtons;

        public MenuRows(List<SingleMenuRow> variants) {
            leftButtons = variants.stream().map(r -> r.left).collect(Collectors.toList());
            rightButtons = variants.stream().map(r -> r.right).collect(Collectors.toList());
        }
    }
    
    public static class GildingMenuButtonHandler {
        public static void onGuiInit(MinecraftClient client, Screen gui, int scaledWidth, int scaledHeight) {
            MenuRows menu = null;
            int rowIdx = 0, offsetX = 0;
            if (gui instanceof TitleScreen) {
                menu = MenuRows.MAIN_MENU;
                rowIdx = MAIN_MENU_CONFIG_BUTTON_ROW.getValue();
                offsetX = MAIN_MENU_CONFIG_BUTTON_OFFSET.getValue();
            } else if (gui instanceof GameMenuScreen) {
                menu = MenuRows.INGAME_MENU;
                rowIdx = IN_GAME_MENU_CONFIG_BUTTON_ROW.getValue();
                offsetX = IN_GAME_MENU_CONFIG_BUTTON_OFFSET.getValue();
            }

            if (rowIdx != 0 && menu != null) {
                boolean onLeft = offsetX < 0;
                String target = (onLeft ? menu.leftButtons : menu.rightButtons).get(rowIdx - 1);

                int offsetX_ = offsetX;
                ((ScreenAccessor) gui).getChildren().stream()
                        .filter(w -> w instanceof ClickableWidget)
                        .map(w -> (ClickableWidget) w)
                        .filter(w -> w.getMessage() instanceof MutableText t && t.getContent() instanceof TranslatableTextContent content && target.equals(content.getKey()))
                        .findFirst()
                        .ifPresent(w -> {
                            Text GILDING_MENU_BUTTON_TEXT = Text.translatable("menu." + SUPER_MOD_ID + ".button").formatted(Formatting.ITALIC);
                            gui.addDrawableChild(new GildingMenuButton(w.getX() + offsetX_ + (onLeft ? -20 : w.getWidth()), w.getY(), GILDING_MENU_BUTTON_TEXT));
                        });
            }

        }
    }

}
