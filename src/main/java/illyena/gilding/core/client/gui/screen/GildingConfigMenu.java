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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static illyena.gilding.GildingInit.*;
import static illyena.gilding.core.config.GildingConfigOptions.*;

@Environment(EnvType.CLIENT)
public class GildingConfigMenu extends ConfigScreen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama")); // todo Panorama
    private final RotatingCubeMapRenderer backgroundRenderer;

    @Nullable
    private ClickableWidget MODDED_ICON_ROWS;

    public GildingConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public GildingConfigMenu(Screen parent) {
        super(SUPER_MOD_ID, parent);
        this.backgroundRenderer = parent instanceof GildingMenuScreen previous ? previous.backgroundRenderer : new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
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
        this.addDrawableChild(new TextWidget(this.width / 2 - 155, this.height / 6 + 12, 60, 16, Text.literal("Main Menu:"), this.textRenderer));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 24, 150));
        this.addDrawableChild(MAIN_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 24, 150));

        this.addDrawableChild(new TextWidget(this.width / 2 - 155, this.height / 6 + 48, 60, 16, Text.literal("Game Menu:"), this.textRenderer));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_ROW.createButton(this.width / 2 - 155, this.height / 6 + 60, 150));
        this.addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_OFFSET.createButton(this.width / 2 + 5, this.height / 6 + 60, 150));

        this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_TYPE.createButton(this.width / 2 - 155, this.height / 6 + 96, 150));
        this.MODDED_ICON_ROWS = this.addDrawableChild(MODDED_WORLD_GEN_BUTTON_ROW.createButton(this.width / 2 + 5, this.height / 6 + 96, 150));
        this.MODDED_ICON_ROWS.visible = false;

    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float f = 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
        RenderSystem.enableBlend();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 1.0f;
        int i = MathHelper.ceil(g * 255.0F) << 24;
        String string = SUPER_MOD_NAME + " Mod v: " + VERSION;
        context.drawTextWithShadow(this.textRenderer, string, 2, this.height - 10, 16777215 | i);

        super.render(context, mouseX, mouseY, delta);
    }

}
