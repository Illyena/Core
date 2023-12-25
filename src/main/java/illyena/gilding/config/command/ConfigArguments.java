package illyena.gilding.config.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import illyena.gilding.compat.Mod;
import illyena.gilding.config.option.ConfigOption;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class ConfigArguments {

    public static void registerArgumentTypes() {
        ArgumentTypes.register("modid", ConfigModIdArgument.class, new ConstantArgumentSerializer<>(ConfigModIdArgument::configModId));
        ArgumentTypes.register("option", ConfigOptionsArgument.class, new ConstantArgumentSerializer<>(ConfigOptionsArgument::configOptions));
    }

    public static class ConfigModIdArgument implements ArgumentType<String> {
        public static final String NAME = "modId";

        private ConfigModIdArgument() { }

        public static ConfigModIdArgument configModId() { return new ConfigModIdArgument(); }

        public static String modId(CommandContext<ServerCommandSource> context, String name) {
            return context.getArgument(name, String.class);
        }

        public String parse(StringReader reader) throws CommandSyntaxException {
            String string = reader.readUnquotedString();
            if (Mod.getFromId(string) != null) {
                if (Mod.getFromId(string).isLoaded()) {
                    return string;
                } else throw new DynamicCommandExceptionType(string1 -> tkUnloadedMod((String) string1)).create(string);
            } else throw new DynamicCommandExceptionType(string2 -> tkIncompatibleMod((String) string2)).create(string);
        }

        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(Mod.loadedModIds().stream(), builder);
        }

        public static Text tkIncompatibleMod(String modId) {
            return new TranslatableText("argument." + SUPER_MOD_ID + ".incompatible_mod", modId);
        }

        public static Text tkUnloadedMod(String modId) {
            return new TranslatableText("argument." + SUPER_MOD_ID + ".unloaded", modId);
        }

    }

    public static class ConfigOptionsArgument implements ArgumentType<ConfigOption<?>> {
        public static final String NAME = "settings";
        private static final DynamicCommandExceptionType NO_SUCH_OPTION = new DynamicCommandExceptionType(string1 -> new TranslatableText("argument." + SUPER_MOD_ID + ".not_option", string1));

        private ConfigOptionsArgument() { }

        public static ConfigOptionsArgument configOptions() {
            return new ConfigOptionsArgument();
        }

        public static ConfigOption<?> getOption(CommandContext<ServerCommandSource> context, String name) {
            return context.getArgument(name, ConfigOption.class);
        }

        public ConfigOption<?> parse(StringReader reader) throws CommandSyntaxException {
            String string = reader.readUnquotedString();
            ConfigOption<?> config = null;
            for (Identifier identifier : ConfigOption.CONFIG.getIds()) {
                if (identifier.getPath().equals(string)) {
                    config = ConfigOption.getConfig(identifier);
                }
            }
            if (config != null) {
                return config;
            }
            else {
                throw NO_SUCH_OPTION.create(string);
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            List<String> options = new ArrayList<>();
            ConfigOption.CONFIG.forEach(config -> {
                if (config.getId().getNamespace().equals(context.getArgument(ConfigModIdArgument.NAME, String.class))) {
                    options.add(config.getKey());
                }
            });

            if (options.isEmpty()) {
                return CommandSource.suggestMatching(new String[]{}, builder);
            } else return CommandSource.suggestMatching(options, builder);
        }

        public static Text tkNotOption(String modId, String option) {
            return new TranslatableText("argument." + SUPER_MOD_ID + ".not_option", option, modId);
        }

    }

}
