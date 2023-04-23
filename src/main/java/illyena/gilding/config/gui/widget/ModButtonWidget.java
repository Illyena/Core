package illyena.gilding.config.gui.widget;

import illyena.gilding.compat.Mod;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModButtonWidget extends ButtonWidget {
    private final Mod mod;

    public ModButtonWidget(Mod mod, int x, int y, int width, int height, Text message, PressAction onPress) {
        this(mod, x, y, width, height, message, onPress, ButtonWidget.EMPTY);

    }

    public ModButtonWidget(Mod mod, int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.mod = mod;
    }

    protected int getYImage(boolean hovered) {
        return this.mod.isLoaded() ? super.getYImage(hovered) : 0;
    }
}
