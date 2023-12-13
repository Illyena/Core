package illyena.gilding.config.option;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import illyena.gilding.config.gui.widget.ConfigSliderWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class IntegerConfigOption extends ConfigOption<Integer> {
    protected final String translationKey;
    protected final int defaultValue;
    protected final int minValue;
    protected int maxValue;
    protected Text tooltip = Text.empty();

    public IntegerConfigOption(String modId, String key, int defaultValue, int min, int max, AccessType accessType, Text tooltip) {
        this(modId, key, defaultValue, min, max, accessType);
        this.tooltip = tooltip;
    }

    public IntegerConfigOption(String modId, String key, int defaultValue, int min, int max, AccessType accessType) {
        super(modId, key, accessType);
        ConfigOptionStorage.setInteger(key, defaultValue);
        this.type = Type.INT;
        this.translationKey = "option." + modId + "." + key;
        this.defaultValue = defaultValue;
        this.minValue = min;
        this.maxValue = max;
    }

    public void setMutableMax(int value) { this.maxValue = Math.max(this.defaultValue, value); }

    public void setValue(Integer value) {
        ConfigOptionStorage.setInteger(key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Integer value) throws CommandSyntaxException {
        if (value >= minValue && value <= maxValue) {
            this.setValue(value);
            this.sync(source);
        } else {
            throw  value > maxValue ?
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().create(value, maxValue) :
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().create(value, minValue);
        }

    }

    public void incrementValue() { incrementValue(1); }

    public void incrementValue(int i) {
        if (ConfigOptionStorage.getInteger(key) == maxValue) {
            ConfigOptionStorage.setInteger(key, minValue);
        } else {
            ConfigOptionStorage.setInteger(key, ConfigOptionStorage.getInteger(key) + i);
        }
    }

    public void decrementValue() { decrementValue(1); }

    public void decrementValue(int i) {
        if (ConfigOptionStorage.getInteger(key) == minValue) {
            ConfigOptionStorage.setInteger(key, maxValue);
        } else {
            ConfigOptionStorage.setInteger(key, ConfigOptionStorage.getInteger(key) - i);
        }
    }

    public Integer getValue() { return ConfigOptionStorage.getInteger(key); }

    public Integer getDefaultValue() { return defaultValue; }

    public int getMinValue() { return minValue; }

    public int getMaxValue() { return maxValue; }

    public Text getValueText() { return Text.literal(String.valueOf(ConfigOptionStorage.getInteger(key))); }

    public Text getButtonText() { return ScreenTexts.composeGenericOptionText(Text.translatable(translationKey), getValueText()); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return new ConfigSliderWidget(this, x, y, width, 20, this.tooltip);
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        setValue(context.getSource(), context.getArgument("value", int.class));
    }

}
