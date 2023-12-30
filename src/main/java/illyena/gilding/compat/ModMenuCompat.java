package illyena.gilding.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import illyena.gilding.core.client.gui.screen.GildingMenuScreen;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() { return GildingMenuScreen::new; }

}
