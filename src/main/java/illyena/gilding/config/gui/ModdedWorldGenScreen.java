package illyena.gilding.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.core.util.time.GildingCalendar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;

public class ModdedWorldGenScreen extends ConfigScreen{
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private final RotatingCubeMapRenderer backgroundRenderer;

    public ModdedWorldGenScreen() { this(MinecraftClient.getInstance().currentScreen); }

    public ModdedWorldGenScreen(Screen parent) {
        super(SUPER_MOD_ID, parent);
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
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

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.backgroundRenderer.render(delta, MathHelper.clamp(1.0f, 0.0F, 1.0F));
        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        int l = MathHelper.ceil(255.0F) << 24;

        drawCenteredText(matrices, this.textRenderer, translationKeyOf("menu", "modded_world_gen_options.title"), this.width / 2, this.height / 8, Color.CYAN.getRGB());
        int m = this.textRenderer.getWidth(GildingCalendar.getDateLong()) / 2;
        drawStringWithShadow(matrices, this.textRenderer, GildingCalendar.getDateLong(), this.width / 2 - m, this.height - 10, 16777215 | l);

        super.render(matrices, mouseX, mouseY, delta);

    }
}
