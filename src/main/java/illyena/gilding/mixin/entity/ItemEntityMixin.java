package illyena.gilding.mixin.entity;

import illyena.gilding.core.item.IUnbreakable;
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
    ItemEntity itemEntity = (ItemEntity) (Object) this;
    @Shadow private int health;
    @Shadow private int itemAge;
    @Shadow public abstract ItemStack getStack();

    @Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onTick(CallbackInfo ci) {
        Item item = this.itemEntity.getStack().getItem();
        boolean bl = this.itemEntity.getY() <= this.itemEntity.getWorld().getBottomY();
        if (!this.itemEntity.getWorld().isClient() && item instanceof IUnbreakable) {
            if (item instanceof BlockEntityItem blockEntityItem) {
                if ((this.itemAge >= 250 && this.itemAge < 5999) || bl) {
                    BlockPos blockPos = new BlockPos(this.itemEntity.getBlockPos().getX(), this.itemEntity.getWorld().getBottomY() + 1, this.itemEntity.getBlockPos().getZ());
                    if (blockEntityItem.toBlock(this.itemEntity.getStack(), this.itemEntity.getWorld(), this.itemEntity, bl ? blockPos : this.itemEntity.getBlockPos(), 0)) {
                        this.itemEntity.discard();
                    } else {
                        this.itemEntity.setNeverDespawn();
                        if (bl) {
                            this.itemEntity.setVelocity(this.itemEntity.getVelocity().getX(), 0, this.itemEntity.getVelocity().getZ());
                            this.itemEntity.setNoGravity(true);
                        }
                    }
                }
            } else {
                this.itemEntity.setNeverDespawn();
                if (bl) {
                    this.itemEntity.setVelocity(this.itemEntity.getVelocity().getX(), 0, this.itemEntity.getVelocity().getZ());
                    this.itemEntity.setNoGravity(true);
                }
            }
        }
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;", ordinal = 3), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Item item = this.getStack().getItem();
        if (item instanceof IUnbreakable) {
            this.health = Math.max(1, this.health - 1);
            if (item instanceof BlockEntityItem blockEntityItem) {
                if (blockEntityItem.toBlock(this.itemEntity.getStack(), this.itemEntity.getWorld(), this.itemEntity, this.itemEntity.getBlockPos(), 0)) {
                    this.itemEntity.discard();
                }
            }
        }
        cir.setReturnValue(true);
        cir.cancel();
    }

}