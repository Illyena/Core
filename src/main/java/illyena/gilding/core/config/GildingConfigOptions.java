package illyena.gilding.core.config;

import illyena.gilding.config.option.IntegerConfigOption;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class GildingConfigOptions {
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mmConfigButtonRow", 2, 0, 4);
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "mmConfigButtonOffsetX", 4, -80, 80);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "inGameMenuConfigButtonRow", 3, 0, 5);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "inGameMenuConfigButtonOffsetX", 4, -100, 100);

    public static void registerConfig() { }

}
