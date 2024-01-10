package illyena.gilding.config.option;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

@SuppressWarnings("unused")
public class BooleanConfigOption extends ConfigOption<Boolean> {
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    private final String translationKey;
    private final boolean defaultValue;
    private final Text enabledText;
    private final Text disabledText;
    private List<OrderedText> tooltip = List.of();

    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType, List<OrderedText> tooltip) {
        this(modId, key, defaultValue, enabledKey, disabledKey, accessType);
        this.tooltip = tooltip;
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType) {
        super(modId, key, accessType);
        ConfigOptionStorage.setBoolean(key, defaultValue);
        this.type = Type.BOOL;
        this.translationKey = "option." + modId + "." + key;
        this.defaultValue = defaultValue;
        this.enabledText = new TranslatableText(this.translationKey + "." + enabledKey);
        this.disabledText = new TranslatableText(this.translationKey + "." + disabledKey);
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, AccessType accessType, List<OrderedText> tooltip) {
        this(modId, key, defaultValue, accessType);
        this.tooltip = tooltip;
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, AccessType accessType) {
        this(modId, key, defaultValue, "true", "false", accessType);
    }

    public void setValue(Boolean value) {
        ConfigOptionStorage.setBoolean(this.key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Boolean value) {
        this.setValue(value);
        this.sync(source);
    }

    public void toggleValue() { ConfigOptionStorage.toggleBoolean(this.key); }

    public Boolean getValue() { return ConfigOptionStorage.getBoolean(this.key); }

    public Boolean getDefaultValue() { return this.defaultValue; }

    @Override
    public boolean validate(Boolean value) { return BOOLEAN_VALUES.contains(value); }

    public Text getValueText() { return this.getValue() ? this.enabledText : this.disabledText; }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(new TranslatableText(this.translationKey), getValue() ? this.enabledText : this.disabledText);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return CyclingButtonWidget.builder(o -> this.getValueText()).values(BOOLEAN_VALUES)
                .tooltip(factory -> this.tooltip)
                .initially(this.getValue())
                .build(x, y, width, 20, new TranslatableText(this.translationKey), ((button, value) -> {
                    this.toggleValue();
                    button.setValue(this.getValue());
                }));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) {
        this.setValue(context.getSource(), context.getArgument("value", Boolean.class));
    }

}
