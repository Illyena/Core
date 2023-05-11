package illyena.gilding.core.config;

import illyena.gilding.config.option.IntegerConfigOption;
import illyena.gilding.config.option.ConfigOption;
import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class GildingConfigOptions {
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mm_config_button_row", 2, 0, 4, ConfigOption.AccessType.CLIENT);
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "mm_config_button_offset", 4, -80, 80, ConfigOption.AccessType.CLIENT);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "gm_config_button_row", 3, 0, 5, ConfigOption.AccessType.CLIENT);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "gm_config_button_offset", 4, -100, 100, ConfigOption.AccessType.CLIENT);

    public static void registerConfig() { }

}
