package illyena.gilding.config.option;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.Option;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class IntegerConfigOption extends ConfigOption<Integer> {
    protected final String translationKey;
    protected final int defaultValue;
    protected final int minValue;
    protected int maxValue;
    protected List<OrderedText> tooltip = List.of();

    public IntegerConfigOption(String modId, String key, int defaultValue, int min, int max, AccessType accessType, List<OrderedText> tooltip) {
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

    public Text getValueText() { return new LiteralText(String.valueOf(ConfigOptionStorage.getInteger(key))); }

    public Text getButtonText() { return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValueText()); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return new DoubleOptionSliderWidget(MinecraftClient.getInstance().options, x, y, width, 20, (DoubleOption) this.asOption(), this.tooltip);
    }

    @Environment(EnvType.CLIENT)
    public Option asOption() {
         return new DoubleOption(translationKey, minValue, maxValue, 1.0f,
                 (gameOptions) -> (double) ConfigOptionStorage.getInteger(key),
                 (gameOptions, value) -> ConfigOptionStorage.setInteger(key, value.intValue()),
                 ((gameOptions, option) -> getButtonText()));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        setValue(context.getSource(), context.getArgument("value", Integer.class));
    }

}