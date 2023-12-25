package illyena.gilding.core.event;

import illyena.gilding.config.gui.widget.ModdedWorldGenButton;
import illyena.gilding.core.client.gui.widget.GildingMenuButton;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;

import static illyena.gilding.GildingInit.SUPER_MOD_ID;

public class ClientEvents {
    private static final Identifier LATE_PHASE = new Identifier(SUPER_MOD_ID, "late");

    public static void registerClientEvents() {
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, LATE_PHASE);
        ScreenEvents.AFTER_INIT.register(LATE_PHASE, GildingMenuButton.GildingMenuButtonHandler::onGuiInit);
        ScreenEvents.AFTER_INIT.register(LATE_PHASE, ModdedWorldGenButton.ModdedWorldGenButtonHandler::onGuiInit);
    }

}
