package illyena.gilding.core.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ConfigScreen;
import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;
import static illyena.gilding.core.config.GildingConfigOptions.*;

@Environment(EnvType.CLIENT)
public class GildingConfigMenu extends ConfigScreen {
    private static final Text TITLE = translationKeyOf("menu", "config.title");
    private static final Text MAIN_MENU_BUTTON_SUB_MENU = translationKeyOf("menu", "config.main_menu");
    private static final Text GAME_MENU_BUTTON_SUB_MENU = translationKeyOf("menu", "config.game_menu");
    private static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier TITLE_TEXTURE = new Identifier(SUPER_MOD_ID, "textures/gui/title/gilding.png");

    @Nullable
    private ClickableWidget MODDED_ICON_ROWS;

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

    public void tick() {
        Objects.requireNonNull(MODDED_ICON_ROWS).visible = MODDED_WORLD_GEN_BUTTON_TYPE.getValue() == ModdedWorldGenButton.Type.ICON;
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
        this.addDrawableChild(new TextWidget(this.width / 2 - 155, this.height / 6 + 12, 60, 16, MAIN_MENU_BUTTON_SUB_MENU, this.textRenderer));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 24, 150));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 24, 150));

        this.addDrawableChild(new TextWidget(this.width / 2 - 155, this.height / 6 + 48, 60, 16, GAME_MENU_BUTTON_SUB_MENU, this.textRenderer));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 60, 150));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 60, 150));

        this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_TYPE.createButton(this.width / 2 - 155, this.height / 6 + 96, 150));
        this.MODDED_ICON_ROWS = this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_ROW.createButton(this.width / 2 + 5, this.height / 6 + 96, 150));
        this.MODDED_ICON_ROWS.visible = false;
    }

    public void renderText(DrawContext context, int mouseX, int mouseY, float delta, float alpha, int time) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        context.drawTexture(TITLE_TEXTURE, this.width / 2 - 80, 10, 0.0f, 0.0f, 160, 60, 160, 60);
    }

    public RotatingCubeMapRenderer getBackgroundRenderer() { return this.backgroundRenderer; }

}
