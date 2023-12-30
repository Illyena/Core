package illyena.gilding.config.option;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import illyena.gilding.config.command.ConfigArguments;
import illyena.gilding.config.command.ConfigCommand;
import illyena.gilding.config.option.util.HasTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
public class EnumConfigOption<E extends Enum<E>> extends ConfigOption<Enum<E>> {
    private final String translationKey;
    private final Class<E> enumClass;
    private final E defaultValue;
    private Text tooltip = Text.empty();

    public EnumConfigOption(String modId, String key, E defaultValue, AccessType accessType, Text tooltip) {
        this(modId, key, defaultValue, accessType);
        this.tooltip = tooltip;
    }

    public EnumConfigOption(String modId, String key, E defaultValue, AccessType accessType) {
        super(modId, key, accessType);
        ConfigOptionStorage.setEnum(key, defaultValue);
        this.type = Type.ENUM;
        this.translationKey = "option." + modId + "." + key;
        this.enumClass = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
    }

    public void setValue(Enum<E> value) {
        ConfigOptionStorage.setEnum(this.key, value);
        this.markDirty();
    }

    public <T extends Enum<T>> void setValue(PacketByteBuf ignored, T value) {
        ConfigOptionStorage.setEnum(this.key, value);
        this.markDirty();
    }

    public void setValue(ServerCommandSource source, Enum<E> value) throws CommandSyntaxException {
        this.setValue(value);
        this.sync(source);
    }

    public void cycleValue() { ConfigOptionStorage.cycleEnum(this.key, this.enumClass); }

    public void cycleValue(int amount) { ConfigOptionStorage.cycleEnum(this.key, this.enumClass, amount); }

    public E getValue() { return ConfigOptionStorage.getEnum(this.key, this.enumClass); }

    public Class<E> getEnumClass() { return this.enumClass; }

    public Enum<E> getDefaultValue() { return this.defaultValue; }

    @Override
    public Text getValueText() { return this.getValueText(this.getValue()); }

    private Text getValueText(E value) { return Text.literal(value.name()); }

    public Text getButtonText() { return Text.translatable(this.translationKey); }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createButton(int x, int y, int width) {
        return CyclingButtonWidget.builder(o -> this.getValueText()).values(this.enumClass.getEnumConstants())
                .tooltip(factory -> this.getValue() instanceof HasTooltip value? Tooltip.of(value.getTooltipText()) : Tooltip.of(this.tooltip))
                .initially(this.getValue())
                .build(x, y, width, 20, Text.translatable(this.translationKey), ((button, value) -> {
                    this.cycleValue();
                    button.setValue(this.getValue());
                }));
    }

    @Override
    public void setFromArgument(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            setValue(context.getSource(), context.getArgument("value", Enum.class));
            context.getSource().sendFeedback(() -> ConfigCommand.tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), this.getKey(), this.getValue().name()), true);
        } catch (IllegalArgumentException | CommandSyntaxException tryAgain) {
            try {
                setValue(context.getSource(), this.getValueFromString(context.getArgument("value", String.class)));
                context.getSource().sendFeedback(() -> ConfigCommand.tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), this.getKey(), this.getValue().name()), true);

            } catch (IllegalArgumentException | CommandSyntaxException e) {
                context.getSource().sendError(Text.of(e.getMessage()));
            }
        }
    }

    public E getValueFromString(String string) {
        E _enum = null;
        for (E e : this.enumClass.getEnumConstants()) {
            if (e.name().equals(string.toUpperCase())) { _enum = e; }
        }
        return _enum;
    }

}
