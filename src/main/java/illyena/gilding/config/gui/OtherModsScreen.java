package illyena.gilding.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.widget.ModButtonWidget;
import illyena.gilding.core.util.time.GildingCalendar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class OtherModsScreen extends Screen{
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final boolean isMinceraft;
    private final RotatingCubeMapRenderer backgroundRenderer;

    private final Screen parent;

    public OtherModsScreen() {
        this(MinecraftClient.getInstance().currentScreen);
    }

    public OtherModsScreen(Screen parent) {
        super(new TranslatableText("menu." + SUPER_MOD_ID + ".other_mods.title"));
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.isMinceraft = (double)(new Random()).nextFloat() < 1.0E-4;
        this.parent = parent;
    }

    protected void init() {
        int l = this.height / 4 + 48;

        this.addDrawableChild(new ButtonWidget( this.width / 2 - 100, this.height / 6 , 200, 20,
                new TranslatableText("menu." + SUPER_MOD_ID + ".other_mods.title"), (button) -> { }));

        this.initMultiWidgets();

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, l + 72 + 12, 98, 20,
                ScreenTexts.BACK, (button) -> this.client.setScreen(this.parent)));
        if (this.client.world != null) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 +12, 98, 20,
                    new TranslatableText("menu.returnToGame"), button -> this.client.setScreen((null))));
        } else {
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20,
                    new TranslatableText("gui.toTitle"), (button) -> this.client.setScreen(new TitleScreen())));
        }
    }

    private void initMultiWidgets() {
        int i = 0;
        List<Mod> modList = Mod.getOtherMods(SUPER_MOD_ID);

        for (Mod mod : modList) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 - 12 + 24 * (i  >> 1) + 48;
            this.addDrawableChild(this.createButton(mod, j, k, 150, 20));
            ++i;
        }
    }

    private ButtonWidget createButton(Mod mod, int x, int y, int width, int height ) {
        Text text = new TranslatableText("menu." + SUPER_MOD_ID + "." + mod.getModId() + "_config.button");
        ButtonWidget.TooltipSupplier tooltipSupplier = new ButtonWidget.TooltipSupplier() {
            private static final Text MOD_INACTIVE_TEXT = new TranslatableText("menu." + SUPER_MOD_ID + ".inactive_mod.tooltip");
            @Override
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                if (button.active) {
                    OtherModsScreen.this.renderTooltip(matrices, MOD_INACTIVE_TEXT, mouseX, mouseY);
                }
            }
            public void supply(Consumer<Text> consumer) { consumer.accept(this.MOD_INACTIVE_TEXT); }
        };

        ButtonWidget buttonWidget = new ModButtonWidget(mod, x, y, width, height, text, (button) -> {
            if (mod.isLoaded()) {
                this.client.setScreen((Screen) Mod.ModScreens.getScreen(mod.getModId(), this.parent));
            }
        }, mod.isLoaded() ? ButtonWidget.EMPTY : tooltipSupplier);

        return buttonWidget;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f = 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f); //this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = 1.0f;
        int l = MathHelper.ceil(g * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
            if (this.isMinceraft) {
                this.drawWithOutline(j, 30, (x, y) -> {
                    this.drawTexture(matrices, x + 0, y, 0, 0, 99, 44);
                    this.drawTexture(matrices, x + 99, y, 129, 0, 27, 44);
                    this.drawTexture(matrices, x + 99 + 26, y, 126, 0, 3, 44);
                    this.drawTexture(matrices, x + 99 + 26 + 3, y, 99, 0, 26, 44);
                    this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                });
            } else {
                this.drawWithOutline(j, 30, (x, y) -> {
                    this.drawTexture(matrices, x + 0, y, 0, 0, 155, 44);
                    this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
                });
            }

            drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
            drawCenteredText(matrices, this.textRenderer, new LiteralText("HAPPY " + GildingCalendar.checkHolidays().name() + "!"), this.width / 2, this.height / 8, Color.CYAN.getRGB());
            int m = this.textRenderer.getWidth(GildingCalendar.getDateLong()) / 2;
            drawStringWithShadow(matrices, this.textRenderer, GildingCalendar.getDateLong(), this.width / 2 - m, this.height - 10, 16777215 | l);

            for (Element element : this.children()) {
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(g);
                }
            }

            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
