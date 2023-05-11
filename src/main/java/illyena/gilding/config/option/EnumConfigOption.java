package illyena.gilding.config.option;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import illyena.gilding.config.command.ConfigArguments;
import illyena.gilding.config.command.ConfigCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class EnumConfigOption<E extends Enum<E>> extends ConfigOption<Enum<E>> {
    private final String translationKey;
    private final Class<E> enumClass;
    private final E defaultValue;
    private List<OrderedText> tooltips = ImmutableList.of();

    public EnumConfigOption(String modId, String key, E defaultValue, AccessType accessType, List<OrderedText> tooltips) {
        this(modId, key, defaultValue, accessType);
        this.tooltips = tooltips;
    }

    public EnumConfigOption(String modId, String key, E defaultValue, AccessType accessType) {
        super(modId, key);
        ConfigOptionStorage.setEnum(key, defaultValue);
        this.type = Type.ENUM;
        this.accessType = accessType;
        this.translationKey = "option." + modId + "." + key;
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

    public void setValue(ServerCommandSource source, Enum<E> value) throws CommandSyntaxException {
        this.setValue(value);
        this.sync(source);
    }

    public void cycleValue() { ConfigOptionStorage.cycleEnum(key, enumClass); }

    public void cycleValue(int amount) { ConfigOptionStorage.cycleEnum(key, enumClass, amount); }

    public E getValue() { return ConfigOptionStorage.getEnum(key, enumClass); }

    public Class<E> getEnumClass() { return this.enumClass; }

    public Enum<E> getDefaultValue() { return defaultValue; }

    @Override
    public Text getValueText() { return this.getValueText(this.getValue()); }

    private Text getValueText(E value) { return Text.literal(value.name()); }

    public Text getButtonText() { return Text.translatable(translationKey); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return CyclingButtonWidget.builder(o -> this.getValueText()).values(this.enumClass.getEnumConstants()).tooltip(factory -> tooltips).initially(this.getValue())
                .build(x, y, width, 20, Text.translatable(translationKey), ((button, value) -> {
                    this.cycleValue();
                    button.setValue(this.getValue());
                }) );
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            setValue(context.getSource(), context.getArgument("value", Enum.class));
            context.getSource().sendFeedback(ConfigCommand.tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), this.getKey(), this.getValue().name()), true);
        } catch (IllegalArgumentException | CommandSyntaxException tryAgain) {
            try {
                setValue(context.getSource(), this.getValueFromString(context.getArgument("value", String.class)));
                context.getSource().sendFeedback(ConfigCommand.tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), this.getKey(), this.getValue().name()), true);

            } catch (IllegalArgumentException | CommandSyntaxException e) {
                context.getSource().sendError(Text.of(e.getMessage()));
            }
        }
    }

    private E getValueFromString(String string) {
        E _enum = null;
        for (E e : this.enumClass.getEnumConstants()) {
            if (e.name().equals(string.toUpperCase())) { _enum = e; }
        }
        return _enum;
    }
}
