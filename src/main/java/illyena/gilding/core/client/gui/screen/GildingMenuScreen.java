package illyena.gilding.core.client.gui.screen;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.gui.ModsScreen;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.widget.ModButtonWidget;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;

import static illyena.gilding.GildingInit.*;

public class GildingMenuScreen extends Screen implements SharedBackground {
    private static final Text TITLE = translationKeyOf("menu", "title");
    private static final Text CONFIG_BUTTON = translationKeyOf("menu", "config.button");
    private static final Text MOD_MENU_BUTTON = Text.translatable("menu." + ModMenu.MOD_ID + ".button");
    private static final Text MOD_INACTIVE_TOOLTIP = translationKeyOf("tooltip", "button.inactive_mod");

    private static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private final RotatingCubeMapRenderer backgroundRenderer;
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;
    private final Screen parent;

    public GildingMenuScreen(Screen parent) {
        super(TITLE);
        if (parent instanceof SharedBackground previous) {
            this.backgroundRenderer = previous.getBackgroundRenderer();
            this.doBackgroundFade = false;
        } else {
            this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
            this.doBackgroundFade = true;
        }
        this.parent = parent;
    }

    protected void init() {
        int l = this.height / 4 + 48;

        this.addDrawableChild(new ButtonWidget( this.width / 2 - 100, this.height / 6 , 200, 20,
                CONFIG_BUTTON, button -> this.client.setScreen(new GildingConfigMenu(this))));

        this.initMultiWidgets();

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, l + 72 + 12, 98, 20,
                ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20,
                MOD_MENU_BUTTON, button -> this.client.setScreen(new ModsScreen(this.parent))));
    }

    private void initMultiWidgets() {
        int i = 0;
        List<Mod> modList = Mod.getModsSansSubGroups(SUPER_MOD_ID);

        for (Mod mod : modList) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 - 12 + 24 * (i  >> 1) + 48;
            this.addDrawableChild(this.createButton(mod, j, k, 150, 20));
            ++i;
        }
    }

    private ButtonWidget createButton(Mod mod, int x, int y, int width, int height ) {
        Text buttonText = Text.translatable("menu." + mod.getModId() + ".config.button");
        ButtonWidget.TooltipSupplier tooltipSupplier = new ButtonWidget.TooltipSupplier() {
            @Override
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                if (button.active) {
                    GildingMenuScreen.this.renderTooltip(matrices, MOD_INACTIVE_TOOLTIP, mouseX, mouseY);
                }
            }
            public void supply(Consumer<Text> consumer) { consumer.accept(MOD_INACTIVE_TOOLTIP); }
        };

        return new ModButtonWidget(mod, x, y, width, height, buttonText, button -> {
            if (mod.isLoaded()) {
                this.client.setScreen(Mod.ModScreens.getScreen(mod.getModId(), this));
            }
        }, mod.isLoaded() ? ButtonWidget.EMPTY : tooltipSupplier);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));

        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(g * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            String string = "Minecraft: " + SharedConstants.getGameVersion().getName() + ", " + SUPER_MOD_NAME + ": " + VERSION;
            drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);

            for (Element element : this.children()) {
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(g);
                }
            }

            super.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
