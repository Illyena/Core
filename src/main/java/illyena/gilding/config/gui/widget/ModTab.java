package illyena.gilding.config.gui.widget;

import illyena.gilding.config.option.ConfigOption;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import static illyena.gilding.GildingInit.translationKeyOf;

public class ModTab extends GridScreenTab {
    private static final Text MOD_TAB_TITLE_TEXT = translationKeyOf("createWorld", "tab.modded.title");

    public ModTab( ) {
        super(MOD_TAB_TITLE_TEXT);
        GridWidget.Adder adder = this.grid.setRowSpacing(8).setColumnSpacing(10).createAdder(2);

        for (ConfigOption<?> config : ConfigOption.CONFIG) {
            if (config.getAccessType() == ConfigOption.AccessType.WORLD_GEN) {
                adder.add(config.createButton(this.grid.getX(), this.grid.getY(), 150));
            }
        }

    }

}

