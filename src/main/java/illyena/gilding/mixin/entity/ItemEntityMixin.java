package illyena.gilding.mixin.entity;

import illyena.gilding.core.item.IUndestroyable;
import illyena.gilding.core.item.util.BlockEntityItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow private int health;
    @Shadow private int itemAge;
    @Shadow public abstract ItemStack getStack();

    @Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        Item item = itemEntity.getStack().getItem();
        boolean bl = itemEntity.getY() <= itemEntity.getWorld().getBottomY();
        if (!itemEntity.getWorld().isClient() && item instanceof IUndestroyable) {
            if (item instanceof BlockEntityItem blockEntityItem) {
                if ((this.itemAge >= 250 && this.itemAge < 5999) || bl) {
                    BlockPos blockPos = new BlockPos(itemEntity.getBlockPos().getX(), itemEntity.getWorld().getBottomY() + 1, itemEntity.getBlockPos().getZ());
                    if (blockEntityItem.toBlock(itemEntity.getStack(), itemEntity.getWorld(), itemEntity, bl ? blockPos : itemEntity.getBlockPos(), 0)) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setNeverDespawn();
                        if (bl) {
                            itemEntity.setVelocity(itemEntity.getVelocity().getX(), 0, itemEntity.getVelocity().getZ());
                            itemEntity.setNoGravity(true);
                        }
                    }
                }
            } else {
                itemEntity.setNeverDespawn();
                if (bl) {
                    itemEntity.setVelocity(itemEntity.getVelocity().getX(), 0, itemEntity.getVelocity().getZ());
                    itemEntity.setNoGravity(true);
                }
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ItemEntity itemEntity = (ItemEntity)(Object)this;
        Item item = this.getStack().getItem();
        if (item instanceof IUndestroyable) {
            this.health = Math.max(1, this.health - 1);
            if (item instanceof BlockEntityItem blockEntityItem) {
                if (blockEntityItem.toBlock(itemEntity.getStack(), itemEntity.getWorld(), itemEntity, itemEntity.getBlockPos(), 0)) {
                    itemEntity.discard();
                }
            }
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}
