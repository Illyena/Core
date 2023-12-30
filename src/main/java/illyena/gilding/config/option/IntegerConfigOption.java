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

@SuppressWarnings("unused")
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
        ConfigOptionStorage.setInteger(this.key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Integer value) throws CommandSyntaxException {
        if (value >= this.minValue && value <= this.maxValue) {
            this.setValue(value);
            this.sync(source);
        } else {
            throw  value > this.maxValue ?
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().create(value, this.maxValue) :
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().create(value, this.minValue);
        }
    }

    public void incrementValue() { this.incrementValue(1); }

    public void incrementValue(int i) {
        if (ConfigOptionStorage.getInteger(this.key) == this.maxValue) {
            ConfigOptionStorage.setInteger(this.key, this.minValue);
        } else {
            ConfigOptionStorage.setInteger(this.key, ConfigOptionStorage.getInteger(this.key) + i);
        }
    }

    public void decrementValue() { decrementValue(1); }

    public void decrementValue(int i) {
        if (ConfigOptionStorage.getInteger(this.key) == this.minValue) {
            ConfigOptionStorage.setInteger(this.key, this.maxValue);
        } else {
            ConfigOptionStorage.setInteger(this.key, ConfigOptionStorage.getInteger(this.key) - i);
        }
    }

    public Integer getValue() { return ConfigOptionStorage.getInteger(this.key); }

    public Integer getDefaultValue() { return this.defaultValue; }

    public int getMinValue() { return this.minValue; }

    public int getMaxValue() { return this.maxValue; }

    public Text getValueText() { return Text.literal(String.valueOf(ConfigOptionStorage.getInteger(this.key))); }

    public Text getButtonText() { return ScreenTexts.composeGenericOptionText(Text.translatable(this.translationKey), getValueText()); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return new ConfigSliderWidget(this, x, y, width, 20, this.tooltip);
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        setValue(context.getSource(), context.getArgument("value", Integer.class));
    }

}
