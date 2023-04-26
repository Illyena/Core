package illyena.gilding.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.GildingInit;
import illyena.gilding.compat.Mod;
import illyena.gilding.core.util.time.GildingCalendar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PlaceHolderScreen extends ConfigScreen{
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    protected RotatingCubeMapRenderer backgroundRenderer;

    public PlaceHolderScreen(String modId) {
        this(modId, MinecraftClient.getInstance().currentScreen);
    }

    public PlaceHolderScreen(String modId, Screen parent) {
        super(modId, parent);
        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
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
            if (this.isMinecraft) {
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
            drawCenteredText(matrices, this.textRenderer, new LiteralText("PLACE HOLDER " + GildingCalendar.checkHolidays().name() + "!"), this.width / 2, this.height / 8, Color.CYAN.getRGB());
            int m = this.textRenderer.getWidth(GildingCalendar.getDateLong()) / 2;
            drawStringWithShadow(matrices, this.textRenderer, GildingCalendar.getDateLong(), this.width / 2 - m, this.height - 10, 16777215 | l);

            for (Element element : this.children()) {
                if (element instanceof ClickableWidget) {
                    ((ClickableWidget) element).setAlpha(g);
                }
            }

            super.render(matrices, mouseX, mouseY, delta);
        } //todo trim renderer
    }
}
