package illyena.gilding.mixin.entity;

import illyena.gilding.core.block.util.LimitedFallingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Shadow private BlockState block;

    @Inject(method = "spawnFromBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onSpawnFromBlock(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<FallingBlockEntity> cir, FallingBlockEntity fallingBlockEntity) {
        BlockEntity blockEntity = world.getBlockEntity(fallingBlockEntity.getFallingBlockPos());
        if (blockEntity != null) {
            fallingBlockEntity.blockEntityData = blockEntity.createNbtWithIdentifyingData();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        FallingBlockEntity fallingBlockEntity = (FallingBlockEntity)(Object)this;
        if (this.block.getBlock() instanceof LimitedFallingBlock limitedFallingBlock && limitedFallingBlock.limit() && !fallingBlockEntity.getWorld().isClient()) {
            limitedFallingBlock.action();
            ci.cancel();
        }
    }

}