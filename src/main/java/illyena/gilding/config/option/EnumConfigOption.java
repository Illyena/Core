package illyena.gilding.config.option;


import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.Option;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;
import java.util.Locale;

public class EnumConfigOption<E extends Enum<E>> extends ConfigOption<Enum<E>> {
    private final String translationKey;
    private final Class<E> enumClass;
    private final Enum<E> defaultValue;
    private List<OrderedText> tooltips = ImmutableList.of();

    public EnumConfigOption(String modId, String key, Enum<E> defaultValue, List<OrderedText> tooltips) {
        this(modId, key, defaultValue);
        this.tooltips = tooltips;
    }

    public EnumConfigOption(String modId, String key, Enum<E> defaultValue) {
        super(modId, key);
        ConfigOptionStorage.setEnum(key, defaultValue);
        this.translationKey = "option" + modId + "." + key;
        this.enumClass = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
    }

    public void setValue(Enum<E> value) {
        ConfigOptionStorage.setEnum(key, value);
        this.markDirty();
    }

    public <T extends Enum<T>> void setValue(PacketByteBuf ignored,T value) {
        ConfigOptionStorage.setEnum(key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Enum<E> value) {
        this.setValue(value);
        this.sync(source);
    }

    public void cycleValue() { ConfigOptionStorage.cycleEnum(key, enumClass); }

    public void cycleValue(int amount) { ConfigOptionStorage.cycleEnum(key, enumClass, amount); }

    public Enum<E> getValue() { return ConfigOptionStorage.getEnum(key, enumClass); }

    public Class<E> getEnumClass() { return this.enumClass; }

    public Enum<E> getDefaultValue() { return defaultValue; }

    @Override
    public Text getValueText() {
        return new TranslatableText(this.translationKey + "." + this.getValue().name().toLowerCase(Locale.ROOT));
    }

    private static <E extends Enum<E>> Text getValueText(EnumConfigOption<E> option, Enum<E> value) {
        return new TranslatableText(option.translationKey + "." + value.name().toLowerCase(Locale.ROOT));
    }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValueText(this, getValue()));
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        CyclingButtonWidget.TooltipFactory<Enum<E>> factory = value -> tooltips;
        return (new CyclingButtonWidget.Builder<Enum<E>>(value -> getValueText(this, value)).values(enumClass.getEnumConstants())
                .tooltip(factory).initially(this.getValue())
                .build(x, y, width, 20, new TranslatableText(this.translationKey), ((button, value) -> ConfigOptionStorage.setEnum(this.getKey(), value))));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public Option asOption() {
        return CyclingOption.create(translationKey, enumClass.getEnumConstants(), value -> getValueText(this, value), ignored -> ConfigOptionStorage.getEnum(key, enumClass), (ignored, option, value) -> ConfigOptionStorage.setEnum(key, value));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) {
        setValue(context.getSource(), context.getArgument("value", enumClass));
    }

}
