package illyena.gilding.core.item;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface Unbreakable {
    public static final UnclampedModelPredicateProvider BROKEN = ModelPredicateProviderRegistry.register(new Identifier("broken"), ((stack, world, entity, seed) ->
            stack.getItem() instanceof Unbreakable item && item.isUsable(stack) ? 0.0f : 1.0f));

    default boolean isUsable(ItemStack stack) { return stack.getDamage() < stack.getMaxDamage() - 1; }
}
