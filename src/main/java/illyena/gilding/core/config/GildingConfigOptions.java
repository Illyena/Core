package illyena.gilding.core.config;

import illyena.gilding.config.option.BooleanConfigOption;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;

import java.util.List;

import static illyena.gilding.GildingInit.*;

public class GildingConfigOptions {
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mmConfigButtonRow", 2, 0, 4, ConfigOption.AccessType.SERVER);
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "mmConfigButtonOffsetX", 4, -80, 80, ConfigOption.AccessType.SERVER);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "inGameMenuConfigButtonRow", 3, 0, 5, ConfigOption.AccessType.SERVER);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "inGameMenuConfigButtonOffsetX", 4, -100, 100, ConfigOption.AccessType.SERVER);

    public static final BooleanConfigOption MODDED_WORLD_GEN_BUTTON_SIZE = new BooleanConfigOption(SUPER_MOD_ID, "mwgButtonSize", false, ConfigOption.AccessType.BOTH,
            List.of(translationKeyOf("tooltip", "mwg_button_config").asOrderedText()));
    public static final IntegerConfigOption MODDED_WORLD_GEN_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mwgButtonRow", 4, 1, 4, ConfigOption.AccessType.BOTH);
    public static final IntegerConfigOption MODDED_WORLD_GEN_BUTTON_OFFSET= new IntegerConfigOption(SUPER_MOD_ID, "mwgButtonOffset", 5, -100, 100, ConfigOption.AccessType.BOTH);

    public static void registerConfig() { }

}
