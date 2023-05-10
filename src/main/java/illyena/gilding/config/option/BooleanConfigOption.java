package illyena.gilding.config.option;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class BooleanConfigOption extends ConfigOption<Boolean> {
    public static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    public final String translationKey;
    public final boolean defaultValue;
    public final Text enabledText;
    public final Text disabledText;
    public List<OrderedText> tooltips = List.of();

    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType, List<OrderedText> tooltips) {
        this(modId, key, defaultValue, enabledKey, disabledKey, accessType);
        this.tooltips = tooltips;
    }
    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType) {
        super(modId, key, accessType);
        ConfigOptionStorage.setBoolean(key, defaultValue);
        this.type = Type.BOOL;
        this.translationKey = "option." + modId + "." + key;
        this.defaultValue = defaultValue;
        this.enabledText = new TranslatableText(translationKey + "." + enabledKey);
        this.disabledText = new TranslatableText(translationKey + "." + disabledKey);
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, AccessType accessType, List<OrderedText> tooltips) {
        this(modId, key, defaultValue, accessType);
        this.tooltips = tooltips;
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, AccessType accessType) {
        this(modId, key, defaultValue, "true", "false", accessType);
    }

    public void setValue(Boolean value) {
        ConfigOptionStorage.setBoolean(key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Boolean value){
        this.setValue(value);
        this.sync(source);
    }

    public void toggleValue() { ConfigOptionStorage.toggleBoolean(key); }

    public Boolean getValue() { return ConfigOptionStorage.getBoolean(key); }

    public Boolean getDefaultValue() { return defaultValue; }

    public Text getValueText() {
        return new LiteralText(String.valueOf(ConfigOptionStorage.getBoolean(key)));
    }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValue() ? enabledText : disabledText);
    }

    public List<OrderedText> getTooltip() { return this.tooltips; }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        CyclingButtonWidget.TooltipFactory<Boolean> tooltipFactory = value -> tooltips;
        return (new CyclingButtonWidget.Builder<Boolean>(value -> value ? this.enabledText : this.disabledText).values(BOOLEAN_VALUES)
                .tooltip(tooltipFactory)).initially(this.getValue())
                .build(x, y, width, 20, new TranslatableText(this.translationKey), ((button, value) -> ConfigOptionStorage.setBoolean(this.getKey(), value)));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public CyclingOption<Boolean> asOption() {
        if (enabledText != null && disabledText != null) {
            return CyclingOption.create(translationKey, enabledText, disabledText,
                    ignored -> ConfigOptionStorage.getBoolean(key),
                    (ignored, option, value) -> ConfigOptionStorage.setBoolean(key, value));
        }
        return CyclingOption.create(translationKey, ignored -> ConfigOptionStorage.getBoolean(key),
                (ignored, option, value) -> ConfigOptionStorage.setBoolean(key, value));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context){
        this.setValue(context.getSource(), context.getArgument("value",  Boolean.class));
    }

}

