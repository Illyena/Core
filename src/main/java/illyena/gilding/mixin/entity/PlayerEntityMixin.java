package illyena.gilding.mixin.entity;

import illyena.gilding.core.item.IUndestroyable;
import illyena.gilding.core.util.data.GildingItemTagGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin that allows custom shields to be damaged, and to be disabled with axes.
 */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        ItemStack stack = ((PlayerEntity)(Object)this).getMainHandStack();
        if (stack.getItem() instanceof IUndestroyable item && !item.isUsable(stack)) {
            ci.cancel();
        }
    }

    @Redirect(method = "damageShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean redirectIsOf(ItemStack instance, Item item) {
        return instance.isOf(item) || instance.isIn(GildingItemTagGenerator.SHIELDS);
    }

    @ModifyArg(method = "damageShield", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"), index = 0)
    private int modifyI(int amount) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack activeStack = player.getActiveItem();
        if (activeStack.getItem() instanceof IUndestroyable) {
            return MathHelper.clamp(amount, 0, activeStack.getMaxDamage() - activeStack.getDamage() - 1);
        } else return amount;

    }

}
