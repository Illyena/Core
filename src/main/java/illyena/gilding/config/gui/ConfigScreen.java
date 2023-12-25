package illyena.gilding.config.gui;

import com.google.common.collect.ImmutableList;
import illyena.gilding.config.ConfigManager;
import illyena.gilding.config.network.ConfigNetworking;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.core.client.gui.screen.SharedBackground;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

import static illyena.gilding.GildingInit.*;

@Environment(EnvType.CLIENT)
public abstract class ConfigScreen extends Screen implements SharedBackground {
    private static final TranslatableText WORLD_GEN_INFO = translationKeyOf("menu", "config_menu.world_gen.info");
    private static final Text NO_SERVER_TOOLTIP = translationKeyOf("tooltip", "config_menu.no_server");

    protected RotatingCubeMapRenderer backgroundRenderer;
    protected boolean doBackgroundFade;
    protected long backgroundFadeStart;
    protected Screen parent;
    protected String modId;

    protected Map< Element, ConfigOption<?>> map = new HashMap<>();

    protected ConfigScreen(String modId, Screen parent, Text title) {
        super(title);
        this.parent = parent;
        this.modId = modId;
    }

    protected List<ConfigOption<?>> getConfigs(String modId) {
        List<ConfigOption<?>> list = new ArrayList<>();
        List<Identifier> ids = new ArrayList<>(ConfigOption.CONFIG.getIds().stream().filter(id -> id.getNamespace().equals(modId)).toList());
        Collections.sort(ids);
        for (Identifier id : ids) {
            list.add(ConfigOption.getConfig(id));
        }
        return list;
    }

    protected void init() {
        this.initSync();

        int l = this.height / 4 + 48;
        this.initMultiWidgets(this.modId);
        this.initBackWidget(l);
        this.initReturnWidget(l);
    }

    protected void initBackWidget(int l) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, l + 72 + 12, 98, 20,
                ScreenTexts.BACK, button -> this.client.setScreen(this.parent)));
    }

    protected void initReturnWidget(int l) {
        if (this.client.world != null) {
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20,
                    new TranslatableText("menu.returnToGame"), button -> this.close()));
        } else {
            this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, l + 72 + 12, 98, 20,
                    new TranslatableText("gui.toTitle"), button -> this.client.setScreen(new TitleScreen())));
        }
    }

    protected void initMultiWidgets(String modId) {
        int i = 0;
        for (ConfigOption<?> config : getConfigs(modId)) {
            int j = this.width / 2 - 155 + i % 2 * 160;
            int k = this.height / 6 - 12 + 24 * (i >> 1) + 48;
            ClickableWidget drawable;
            if (!this.inactivateButton(config)) {
                drawable = this.addDrawableChild(config.createButton(j, k, 150));
            } else {
                drawable = this.addDrawableChild(this.createDeadButton(config, j, k, 150));
            }
            this.map.put(drawable, config);
            ++i;
        }
    }

    protected void initSync() {
        if (this.client.world != null) {
            ClientPlayNetworking.send(ConfigNetworking.CONFIG_RETRIEVE_C2S, PacketByteBufs.create());
        }
    }

    protected boolean inactivateButton(ConfigOption<?> config) {
        return switch (config.getAccessType()) {
            case BOTH -> false;
            case CLIENT -> this.client == null || this.client.world == null;
            case SERVER, WORLD_GEN -> this.client != null && this.client.world != null;
        };
    }

    protected ClickableWidget createDeadButton(ConfigOption<?> config, int x, int y, int width) {
        ButtonWidget.TooltipSupplier tooltips = new ButtonWidget.TooltipSupplier() {
            @Override
            public void onTooltip(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) {
                if (button.active && config.getAccessType().equals(ConfigOption.AccessType.CLIENT)) {
                    ConfigScreen.this.renderTooltip(matrices, NO_SERVER_TOOLTIP, mouseX, mouseY);
                }
            }
            public void supply(Consumer<Text> consumer) { consumer.accept(NO_SERVER_TOOLTIP); }
        };

        return new ButtonWidget( x, y, width, 20, config.getButtonText(), button -> {}, tooltips) {
            @Override
            protected int getYImage(boolean hovered) { return 0; }

        };
    }

    @Override
    public void removed() { ConfigManager.save(); }

    public void close() {
        ConfigOption.getConfigs(this.modId).forEach(ConfigOption::sync);
        super.close();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }

        float f = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        this.getBackgroundRenderer().render(delta, MathHelper.clamp(f, 0.0F, 1.0F));

        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(g * 255.0F) << 24;
        if ((l & -67108864) != 0) {
            this.renderText(matrices, mouseX, mouseY, delta, g, l);
            String string = "Minecraft: " + SharedConstants.getGameVersion().getName() + ", " + SUPER_MOD_NAME + ": " + VERSION;
            drawStringWithShadow(matrices, this.textRenderer, string, 2, this.height - 10, 16777215 | l);

            for (Element element : this.children()) {
                if (element instanceof ClickableWidget clickable) {
                    clickable.setAlpha(g);
                    drawInfo(clickable, matrices);
                }
            }

            super.render(matrices, mouseX, mouseY, delta);

            List<OrderedText> list = getHoveredButtonTooltip(mouseX, mouseY);
            if (list != null) {
                this.renderOrderedTooltip(matrices, list, mouseX, mouseY);
            }
        }
    }

    protected abstract void renderText(MatrixStack matrices, int mouseX, int mouseY, float delta, float alpha, int time);

    private void drawInfo(ClickableWidget widget, MatrixStack matrices) {
        if (this.map.get(widget) != null) {
            switch (this.map.get(widget).getAccessType()) {
                case WORLD_GEN -> drawTextWithShadow(matrices, this.textRenderer, WORLD_GEN_INFO, widget.x + 4, widget.y + 22, Color.GRAY.getRGB());
                default -> { }
            }
        }
    }

    public List<OrderedText> getHoveredButtonTooltip(int mouseX, int mouseY) {
        Optional<ClickableWidget> optional = this.getHoveredButton(mouseX, mouseY);
        return optional.isPresent() && optional.get() instanceof OrderableTooltip ? ((OrderableTooltip)optional.get()).getOrderedTooltip() : ImmutableList.of();
    }

    public Optional<ClickableWidget> getHoveredButton(double mouseX, double mouseY) {
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget) {
                if (widget.isMouseOver(mouseX, mouseY)) {
                    return Optional.of(widget);
                }
            }
        }
        return Optional.empty();
    }

    public abstract RotatingCubeMapRenderer getBackgroundRenderer();

}
