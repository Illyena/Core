package illyena.gilding.core.config;

import illyena.gilding.compat.Mod;
import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.EnumConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;
import static illyena.gilding.GildingInit.translationKeyOf;

public class GildingConfigOptions implements Mod.Configs {
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mm_config_button_row", 2, 1, 4,ConfigOption.AccessType.BOTH);
    public static final IntegerConfigOption MAIN_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "mm_config_button_offset", 4, -80, 80, ConfigOption.AccessType.BOTH);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "gm_config_button_row", 3, 1, 5, ConfigOption.AccessType.BOTH);
    public static final IntegerConfigOption IN_GAME_MENU_CONFIG_BUTTON_OFFSET = new IntegerConfigOption(SUPER_MOD_ID, "gm_config_button_offset", 4, -80, 80, ConfigOption.AccessType.BOTH);

    public static final EnumConfigOption<ModdedWorldGenButton.Type> MODDED_WORLD_GEN_BUTTON_TYPE = new EnumConfigOption<>(SUPER_MOD_ID, "mwg_button_type", ModdedWorldGenButton.Type.BUTTON, ConfigOption.AccessType.BOTH,
            translationKeyOf("tooltip", "option.mwg_button_config"));
    public static final IntegerConfigOption MODDED_WORLD_GEN_BUTTON_ROW = new IntegerConfigOption(SUPER_MOD_ID, "mwg_button_row", 3, 1, 5, ConfigOption.AccessType.BOTH);

}
