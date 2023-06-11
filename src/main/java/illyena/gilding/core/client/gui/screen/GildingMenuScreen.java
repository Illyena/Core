package illyena.gilding.core.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.gui.ModsScreen;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.widget.ModButtonWidget;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Random;

import static illyena.gilding.GildingInit.*;

public class GildingMenuScreen extends Screen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final boolean isMinceraft;
    final RotatingCubeMapRenderer backgroundRenderer;

    private final Screen parent;

    public GildingMenuScreen(Screen parent) {
        super(Text.translatable("menu." + SUPER_MOD_ID + ".title"));
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.isMinceraft = (double)(new Random()).nextFloat() < 1.0E-4;
        this.parent = parent;
    }

    protected void init() {
        int l = this.height / 4 + 48;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu." + SUPER_MOD_ID + "." + SUPER_MOD_ID + "_config.button"),
                button -> this.client.setScreen(new GildingConfigMenu(this)))
                .dimensions(this.width / 2 - 100, this.height / 6, 200, 20).build());

        this.initMultiWidgets();

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> this.client.setScreen(this.parent))
                .dimensions(this.width / 2 -100, l + 72 + 12, 98, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu." + SUPER_MOD_ID + ".modmenu.button"),
                button -> this.client.setScreen(new ModsScreen(this.parent)))
                .dimensions(this.width / 2 + 2, l + 72 + 12, 98, 20).build());
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
        Text text = Text.translatable("menu." + SUPER_MOD_ID + "." + mod.getModId() + "_config.button");
        Text MOD_INACTIVE_TEXT = Text.translatable("menu." + SUPER_MOD_ID + ".inactive_mod.tooltip");

        return ModButtonWidget.builder(mod, text, button -> {
            if (mod.isLoaded()) {
                this.client.setScreen(Mod.ModScreens.getScreen(mod.getModId(), this));
            }
        }).dimensions(x, y, width, height).tooltip(Tooltip.of(mod.isLoaded() ? text : MOD_INACTIVE_TEXT)).build();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float f = 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        RenderSystem.enableBlend();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float g = 1.0F;
        int i = MathHelper.ceil(g * 255.0F) << 24;

        String string = SUPER_MOD_NAME + " Mod v: " + VERSION;
        context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | i);
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget) {
                ((ClickableWidget) element).setAlpha(g);
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

}
