package illyena.gilding.core.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.*;

@Environment(EnvType.CLIENT)
public class GildingConfigMenu extends ConfigScreen {
    private static final TranslatableText TITLE = translationKeyOf("menu", "config.title");
    private static final TranslatableText MAIN_MENU_BUTTON_SUB_MENU = translationKeyOf("menu", "config.main_menu");
    private static final TranslatableText GAME_MENU_BUTTON_SUB_MENU = translationKeyOf("menu", "config.game_menu");
    private static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier TITLE_TEXTURE = new Identifier(SUPER_MOD_ID,"textures/gui/title/gilding.png");

    public GildingConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public GildingConfigMenu(Screen parent) {
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
        assert this.client != null;
        int l = this.height / 4 + 48;

        this.initWidgetLayout(l);
        this.initBackWidget(l);
        this.initReturnWidget(l);
    }

    private void initWidgetLayout(int l) {
        this.addDrawableChild(new PressableTextWidget(this.width / 2 - 155, this.height / 6 + 12, 60, 16, MAIN_MENU_BUTTON_SUB_MENU, button -> {}, this.textRenderer));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 24, 150));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 24, 150));

        this.addDrawableChild(new PressableTextWidget(this.width / 2 - 155, this.height / 6 + 48, 60, 16, GAME_MENU_BUTTON_SUB_MENU, button -> {},  this.textRenderer));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 60, 150));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 60, 150));

        this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_SIZE.createButton(this.width / 2 - 155, this.height / 6 + 96, 150));
        this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_ROW.createButton(this.width / 2 -155, this.height / 6 + 120, 150));
        this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 120, 150));
    }

    public void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta, float alpha, int time) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        drawTexture(matrices, this.width / 2 - 80, 10, 0.0f, 0.0f, 160, 60, 160, 60);
    }

    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
