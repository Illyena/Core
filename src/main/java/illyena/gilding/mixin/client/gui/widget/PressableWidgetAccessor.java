package illyena.gilding.mixin.client.gui.widget;

import net.minecraft.client.gui.widget.PressableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PressableWidget.class)
public interface PressableWidgetAccessor {
    @Invoker
    int callGetTextureY();
}
