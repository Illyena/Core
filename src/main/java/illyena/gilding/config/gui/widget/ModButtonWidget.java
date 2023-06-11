package illyena.gilding.config.gui.widget;

import illyena.gilding.compat.Mod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ModButtonWidget extends ButtonWidget {
    private final Mod mod;

    public static Builder builder(Mod mod, Text message, PressAction onPress) { return new Builder(mod, message, onPress); }

    private ModButtonWidget(Mod mod, int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier tooltipSupplier) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.mod = mod;
    }

    @Override
    public int getTextureY() {
        return this.active ? super.getTextureY() : 0;
    }

    @Environment(EnvType.CLIENT)
    public static class Builder {
        private final Mod mod;
        private final Text message;
        private final PressAction onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private NarrationSupplier narrationSupplier;

        public Builder(Mod mod, Text message, PressAction onPress) {
            this.mod = mod;
            this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
            this.message = message;
            this.onPress = onPress;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            return this.position(x, y).size(width, height);
        }

        public Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public ButtonWidget build() {
            ButtonWidget buttonWidget = new ModButtonWidget(this.mod, this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}
