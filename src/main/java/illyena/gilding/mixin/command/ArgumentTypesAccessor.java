package illyena.gilding.mixin.command;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArgumentTypes.class)
public interface ArgumentTypesAccessor {
    @Invoker
    static ArgumentSerializer<?, ?> callRegister(Registry<ArgumentSerializer<?, ?>> registry, String id, Class<?> clazz, ArgumentSerializer<?, ?> serializer) {
        throw new UnsupportedOperationException();
    }
}
