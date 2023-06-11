package illyena.gilding.config.gui.widget;

import illyena.gilding.config.option.ConfigOption;
import illyena.gilding.config.option.IntegerConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ConfigSliderWidget extends SliderWidget {
    protected final IntegerConfigOption config;
    private final Tooltip tooltip;

    public ConfigSliderWidget(ConfigOption<Integer> config, int x, int y, int width, int height, Tooltip tooltip) {
        super(x, y, width, height, Text.literal("TEST"), MathHelper.map((float)config.getValue(), (float)((IntegerConfigOption)config).getMinValue(), (float)((IntegerConfigOption)config).getMaxValue(), 0.0F, 1.0F));
        this.config = (IntegerConfigOption) config;
        this.tooltip = tooltip;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() { this.setMessage(this.config.getButtonText()); }

    @Override
    protected void applyValue() { this.config.setValue(this.toValue(this.value)); }

    protected Integer toValue(double d) {
        return MathHelper.floor(MathHelper.map(d, 0.0, 1.0, this.config.getMinValue(), this.config.getMaxValue()));
    }

}
