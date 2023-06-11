package illyena.gilding.config.option;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class BooleanConfigOption extends ConfigOption<Boolean> {
    private static final List<Boolean> BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
    private final String translationKey;
    private final boolean defaultValue;
    private final Text enabledText;
    private final Text disabledText;
    private Text tooltip;

    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType, Text tooltip) {
        this(modId, key, defaultValue, enabledKey, disabledKey, accessType);
        this.tooltip = tooltip;
    }
    public BooleanConfigOption(String modId, String key, boolean defaultValue, String enabledKey, String disabledKey, AccessType accessType) {
        super(modId, key, accessType);
        ConfigOptionStorage.setBoolean(key, defaultValue);
        this.type = Type.BOOL;
        this.translationKey = "option." + modId + "." + key;
        this.defaultValue = defaultValue;
        this.enabledText = Text.translatable(translationKey + "." + enabledKey);
        this.disabledText = Text.translatable(translationKey + "." + disabledKey);
    }

    public BooleanConfigOption(String modId, String key, boolean defaultValue, AccessType accessType, Text tooltip) {
        this(modId, key, defaultValue, accessType);
        this.tooltip = tooltip;
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

    public Text getValueText() { return Text.translatable(String.valueOf(ConfigOptionStorage.getBoolean(key))); }

    public Text getButtonText() { return ScreenTexts.composeGenericOptionText(Text.translatable(translationKey), getValue() ? enabledText : disabledText); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return CyclingButtonWidget.builder(o -> this.getValueText()).values(BOOLEAN_VALUES).tooltip(tt -> Tooltip.of(this.tooltip)).initially(this.getValue())
                .build(x, y, width, 20, Text.translatable(translationKey), ((button, value) -> {
                    this.toggleValue();
                    button.setValue(this.getValue());
                }));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context){
        this.setValue(context.getSource(), context.getArgument("value",  Boolean.class));
    }

}

