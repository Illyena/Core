package illyena.gilding.core.client.gui.screen;

import com.terraformersmc.modmenu.gui.ModsScreen;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.widget.ModButtonWidget;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;

import static illyena.gilding.GildingInit.*;

public class GildingMenuScreen extends Screen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    final RotatingCubeMapRenderer backgroundRenderer;

    private final Screen parent;

    public GildingMenuScreen(Screen parent) {
        super(Text.translatable("menu." + SUPER_MOD_ID + ".title"));
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.parent = parent;
    }

    protected void init() {
        int l = this.height / 4 + 48;

        this.addDrawableChild(new ButtonWidget( this.width / 2 - 100, this.height / 6 , 200, 20,
                Text.translatable("menu." + SUPER_MOD_ID + "." + SUPER_MOD_ID + "_config.button"),
                button -> this.client.setScreen(new GildingConfigMenu(this))));

        this.initMultiWidgets();

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, l + 72 + 12, 98, 20,
                ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));

        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20,
                Text.translatable("menu." + SUPER_MOD_ID + ".modmenu.button"), (button) -> this.client.setScreen(new ModsScreen(this.parent))));
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
        ButtonWidget.TooltipSupplier tooltipSupplier = new ButtonWidget.TooltipSupplier() {
            private static final Text MOD_INACTIVE_TEXT = Text.translatable("menu." + SUPER_MOD_ID + ".inactive_mod.tooltip");
            @Override
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                if (button.active) {
                    GildingMenuScreen.this.renderTooltip(matrices, MOD_INACTIVE_TEXT, mouseX, mouseY);
                }
            }
            public void supply(Consumer<Text> consumer) { consumer.accept(this.MOD_INACTIVE_TEXT); }
        };

        return new ModButtonWidget(mod, x, y, width, height, text, (button) -> {
            if (mod.isLoaded()) {
                this.client.setScreen(Mod.ModScreens.getScreen(mod.getModId(), this));
            }
        }, mod.isLoaded() ? ButtonWidget.EMPTY : tooltipSupplier);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f = 1.0F;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        float g = 1.0f;
        int l = MathHelper.ceil(g * 255.0F) << 24;

        String string = "Minecraft: " + SharedConstants.getGameVersion().getName() + ", " + SUPER_MOD_NAME + ": " + VERSION;
        drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);

        super.render(matrices, mouseX, mouseY, delta);
    }

}
