package illyena.gilding.config.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModButtonWidget extends ButtonWidget {
    public static Builder builder(Text message, PressAction onPress) { return new Builder(message, onPress); }

    public ModButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    @Override
    public int getTextureY() { return this. active ? super.getTextureY() : 0; }

}
