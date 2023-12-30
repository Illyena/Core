package illyena.gilding.config.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.option.BooleanConfigOption;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;

import java.util.List;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class ConfigCommand {

    public static void registerConfigCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("config")
                .then(CommandManager.argument(ConfigArguments.ConfigModIdArgument.NAME, ConfigArguments.ConfigModIdArgument.configModId())
                        .executes(context -> executeQuerySettings(context.getSource(), ConfigArguments.ConfigModIdArgument.modId(context, ConfigArguments.ConfigModIdArgument.NAME)))
                        .then(CommandManager.argument(ConfigArguments.ConfigOptionsArgument.NAME, ConfigArguments.ConfigOptionsArgument.configOptions())
                                .executes(context -> executeQuerySetting(context.getSource(), ConfigArguments.ConfigModIdArgument.modId(context, ConfigArguments.ConfigModIdArgument.NAME),
                                        ConfigArguments.ConfigOptionsArgument.getOption(context, ConfigArguments.ConfigOptionsArgument.NAME)))
                                .then(CommandManager.argument("value", StringArgumentType.word())
                                        .executes(context -> executeSet(context, ConfigArguments.ConfigModIdArgument.modId(context, ConfigArguments.ConfigModIdArgument.NAME),
                                                ConfigArguments.ConfigOptionsArgument.getOption(context, ConfigArguments.ConfigOptionsArgument.NAME), StringArgumentType.getString(context, "value")))))));
    }

    static int executeSet(CommandContext<ServerCommandSource> context, String modId, ConfigOption<?> option, String value) {
        if (Mod.getFromId(modId) == null) {
            throw new CommandException(ConfigArguments.ConfigModIdArgument.tkIncompatibleMod(modId));
        } else if (!Mod.getFromId(modId).isLoaded()) {
            throw new CommandException(ConfigArguments.ConfigModIdArgument.tkUnloadedMod(modId));
        } else if (!ConfigOption.CONFIG.containsId(new Identifier(modId, option.getKey()))) {
            throw new CommandException((ConfigArguments.ConfigOptionsArgument.tkNotOption(modId, option.getKey())));
        }

        try {
            switch (option.getType()) {
                case INT -> setInt(context, option, value);
                case BOOL -> setBool(context, option, value);
                default -> option.setFromArgument(context);
            }

            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Texts.toText(e.getRawMessage()));
            return -1;
        }
    }

    static int executeQuerySetting(ServerCommandSource source, String modId, ConfigOption<?> option) {
        if (Mod.getFromId(modId) != null) {
            if (Mod.getFromId(modId).isLoaded()) {
                source.sendFeedback(() -> Text.literal(option.getKey() + ": " + option.getValueText().getString()), false);
                return 1;
            } else {
                throw new CommandException(ConfigArguments.ConfigModIdArgument.tkUnloadedMod(modId));
            }
        } else {
                throw new CommandException(ConfigArguments.ConfigModIdArgument.tkIncompatibleMod(modId));
        }
    }

    static int executeQuerySettings(ServerCommandSource source, String modId) {
        List<ConfigOption<?>> options = ConfigOption.getConfigs(modId);
        if (Mod.getFromId(modId) != null) {
            if (Mod.getFromId(modId).isLoaded() && !options.isEmpty()) {
                source.sendFeedback(() -> Text.translatable("argument." + SUPER_MOD_ID + ".settings", modId), false);
                for (ConfigOption<?> config : options) {
                    source.sendFeedback(() -> Text.literal(config.getKey() + ": " + config.getValueText().getString()), false);
                }
                return options.size();
            } else throw new CommandException(ConfigArguments.ConfigModIdArgument.tkUnloadedMod(modId));

        } else {
            throw new CommandException(ConfigArguments.ConfigModIdArgument.tkIncompatibleMod(modId));
        }
    }

    private static void setInt(CommandContext<ServerCommandSource> context, ConfigOption<?> option, String value) {
        try {
            int i = new StringReader(value).readInt();
            ((IntegerConfigOption) option).setValue(context.getSource(), i);
            context.getSource().sendFeedback(() -> tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), option.getKey(), value), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Texts.toText(e.getRawMessage()));
        }
    }

    private static void setBool(CommandContext<ServerCommandSource> context, ConfigOption<?> option, String value) {
        try {
            boolean bool = new StringReader(value).readBoolean();
            ((BooleanConfigOption) option).setValue(context.getSource(), bool);
            context.getSource().sendFeedback(() -> tkSet(context.getArgument(ConfigArguments.ConfigModIdArgument.NAME, String.class), option.getKey(), value), true);
        } catch (CommandSyntaxException e) {
            context.getSource().sendError(Texts.toText(e.getRawMessage()));
        }
    }

    public static Text tkSet(String modId, String option, String value) {
        return Text.translatable("argument." + SUPER_MOD_ID + ".set_success", modId, option, value);
    }

}
