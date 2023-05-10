package illyena.gilding.core.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import illyena.gilding.config.gui.ConfigScreen;
import illyena.gilding.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.atomic.AtomicInteger;

import static illyena.gilding.GildingInit.*;
import static illyena.gilding.config.option.BooleanConfigOption.BOOLEAN_VALUES;
import static illyena.gilding.core.config.GildingConfigOptions.*;

@Environment(EnvType.CLIENT)
public class GildingConfigMenu extends ConfigScreen {
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private final RotatingCubeMapRenderer backgroundRenderer;
    private ClickableWidget mmButtonRow;
    private ClickableWidget mmButtonOffset;
    private ClickableWidget inGameButtonRow;
    private ClickableWidget inGameButtonOffset;
    private CyclingButtonWidget<Boolean> moddedWorldGenButtonSize;
    private ClickableWidget moddedWorldGenButtonRow;
    private ClickableWidget moddedWorldGenButtonOffset;

    public GildingConfigMenu() { this(MinecraftClient.getInstance().currentScreen); }

    public GildingConfigMenu(Screen parent) {
        super(SUPER_MOD_ID, parent);
//        this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
        this.backgroundRenderer = parent instanceof GildingMenuScreen previous ? previous.backgroundRenderer : new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    }

    public void init() {
        this.initSync();

        AtomicInteger i = new AtomicInteger();
//        int j = this.width / 2 - 155 + i % 2 * 160;
//        int k = this.height / 6 - 12 + 24 * (i >> 1) + 48;
        int l = this.height / 4 + 48;

        this.mmButtonRow = addDrawableChild(MAIN_MENU_CONFIG_BUTTON_ROW.createButton(setX(i.get()), setY(i.get()) , 150)); i.incrementAndGet();
        this.mmButtonOffset = addDrawableChild(MAIN_MENU_CONFIG_BUTTON_OFFSET.createButton(setX(i.get()), setY(i.get()), 150)); i.incrementAndGet();

        this.inGameButtonRow = addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_ROW.createButton(setX(i.get()), setY(i.get()), 150)); i.incrementAndGet();
        this.inGameButtonOffset = addDrawableChild(IN_GAME_MENU_CONFIG_BUTTON_OFFSET.createButton(setX(i.get()), setY(i.get()), 150)); i.incrementAndGet();

 //       this.moddedWorldGenButtonSize = addDrawableChild(MODDED_WORLD_GEN_BUTTON_SIZE.createButton(setX(i), setY(i), 150)); ++i;
        CyclingButtonWidget.TooltipFactory<Boolean> tooltipFactory = value -> MODDED_WORLD_GEN_BUTTON_SIZE.getTooltip();
        this.moddedWorldGenButtonSize = addDrawableChild(new CyclingButtonWidget.Builder<Boolean>(value -> value ? MODDED_WORLD_GEN_BUTTON_SIZE.enabledText : MODDED_WORLD_GEN_BUTTON_SIZE.disabledText).values(BOOLEAN_VALUES)
                .tooltip(tooltipFactory).initially(MODDED_WORLD_GEN_BUTTON_SIZE.getValue())
                .build(setX(i.get()), setY(i.get()), 150, 20, new TranslatableText(MODDED_WORLD_GEN_BUTTON_SIZE.translationKey), ((button, value) -> {
                    ConfigOption.ConfigOptionStorage.setBoolean(MODDED_WORLD_GEN_BUTTON_SIZE.getKey(), value);
                    i.set(this.setVisible(i.get()));
                }))); i.incrementAndGet();

        this.moddedWorldGenButtonRow = addDrawableChild(MODDED_WORLD_GEN_BUTTON_ROW.createButton(setX(i.get() + 2), setY(i.get()), 150));
        this.moddedWorldGenButtonRow.visible = false;
        this.moddedWorldGenButtonOffset = addDrawableChild(MODDED_WORLD_GEN_BUTTON_OFFSET.createButton(setX(i.get() + 4), setY(i.get()), 150));
        this.moddedWorldGenButtonOffset.visible = false;

        addDrawableChild(new ButtonWidget(setX(i.get()), setY(i.get()), 150, 20, new LiteralText("TEST"), button -> { })); i.incrementAndGet();
        addDrawableChild(new ButtonWidget(setX(i.get()), setY(i.get()), 150, 20, new LiteralText("TEST 2"), button -> { })); i.incrementAndGet();

        this.initBackWidget(l);
        this.initReturnWidget(l);
    }

    private int setX(int i) { return this.width / 2 - 155 + i % 2 * 160; }
    private int setY(int i) { return this.height / 6 - 12 + 24 * (i >> 1) + 48; }

    private int setVisible(int i) {
        LOGGER.error("value: {}, vis1 {}, vis2 {}", this.moddedWorldGenButtonSize.getValue(), this.moddedWorldGenButtonRow.visible, this.moddedWorldGenButtonOffset.visible);

        this.moddedWorldGenButtonRow.visible = this.moddedWorldGenButtonSize.getValue();
        i = this.moddedWorldGenButtonOffset.visible ? i + 2 : i;
        this.moddedWorldGenButtonOffset.visible = this.moddedWorldGenButtonSize.getValue();
        i = this.moddedWorldGenButtonOffset.visible ? i + 2 : i;

        return i;
    }
    protected boolean inactivateButton() { return false; }

    public void close() {
        this.setVisible(0);
        super.close();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f = 1.0f;
        this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
//        int j = this.width / 2 - 137;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        int l = MathHelper.ceil(255.0F) << 24;
//        drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);
        String string = SUPER_MOD_NAME + " Mod : " + VERSION;
        drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);

        super.render(matrices, mouseX, mouseY, delta);
    }

}
