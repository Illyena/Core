package illyena.gilding.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.core.client.gui.screen.SharedBackground;
import illyena.gilding.core.util.time.GildingCalendar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;

public class ModdedWorldGenScreen extends ConfigScreen implements SharedBackground {
    private static final Text TITLE = translationKeyOf("menu", "modded_world_gen.title");
    private static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier TITLE_TEXTURE = new Identifier(SUPER_MOD_ID, "textures/gui/title/gilding.png");

    public ModdedWorldGenScreen() { this(MinecraftClient.getInstance().currentScreen); }

    public ModdedWorldGenScreen(Screen parent) {
        super(SUPER_MOD_ID, parent, TITLE);
        if (parent instanceof SharedBackground previous) {
            this.backgroundRenderer = previous.getBackgroundRenderer();
            this.doBackgroundFade = false;
        } else {
            this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
            this.doBackgroundFade = true;
        }
    }

    protected void init() {
        this.initSync();

        int l = this.height / 4 + 48;
        this.initMultiWidgets();
        this.initBackWidget(l);
        this.initReturnWidget(l);
    }

    private void initMultiWidgets() {
        int i = 0;
        for (ConfigOption<?> config : ConfigOption.CONFIG) {
            if (config.getAccessType() == ConfigOption.AccessType.WORLD_GEN) {
                int j = this.width / 2 - 155 + i % 2 * 160;
                int k = this.height / 6 - 12 + 24 * (i >> 1) + 48;
                this.addDrawableChild(config.createButton(j, k, 150));

                ++i;
            }
        }
    }

    public void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta, float alpha, int time) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        drawTexture(matrices, this.width / 2 - 80, 10, 0.0f, 0.0f, 160, 60, 160, 60);

        drawCenteredTextWithShadow(matrices, this.textRenderer, Text.of(GildingCalendar.getDateLong()).asOrderedText(), this.width / 2, this.height / 8 * 2, 16777215 | time);
    }

    @Override
    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
