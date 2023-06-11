package illyena.gilding.core.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static illyena.gilding.GildingInit.*;

@Environment(EnvType.CLIENT)
public class GildingConfigMenu extends ConfigScreen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final RotatingCubeMapRenderer backgroundRenderer;

    public GildingConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public GildingConfigMenu(Screen parent) {
        super(SUPER_MOD_ID, parent);
//        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.backgroundRenderer = parent instanceof GildingMenuScreen previous ? previous.backgroundRenderer : new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    }

    protected void init() {
        this.initSync();
        assert this.client != null;
        int l = this.height / 4 + 48;

        this.initMultiWidgets(this.modId);
        this.initBackWidget(l);
        this.initReturnWidget(l);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float f = 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        RenderSystem.enableBlend();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        context.drawTexture(PANORAMA_OVERLAY, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 1.0f;
        int i = MathHelper.ceil(g * 255.0F) << 24;
        String string = SUPER_MOD_NAME + " Mod v: " + VERSION;
        context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | i);

        super.render(context, mouseX, mouseY, delta);
    }

}
