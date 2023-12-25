package illyena.gilding.core.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

/**
 * <pre>
 * INSERT
 *
 * insert fields:
 *      {@code private static final TrackedData<Integer> LOYALTY;
 * private static final TrackedData<Boolean> ENCHANTED;
 * private boolean dealtDamage;
 * private int returnTimer;}
 *
 * insert at init:
 *          {@code this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(stack));
 * this.dataTracker.set(ENCHANTED, stack.hasGlint());}
 *
 * insert at #initDataTracker():
 *          {@code this.dataTracker.startTracking(LOYALTY, (byte)0);
 * this.dataTracker.startTracking(ENCHANTED, false);}
 *</pre>
 */
@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface ILoyalty {
    public abstract int getInGroundTime();

    public abstract void setInGroundTime(int value);

    public abstract ItemStack asItemStack();

    public abstract TrackedData<Integer> getLoyalty();

    public abstract boolean getDealtDamage();

    public abstract int getReturnTimer();

    public abstract void setReturnTimer(int value);

    public abstract int getWait();

    public abstract void setWait(int value);

    public static PersistentProjectileEntity getProjectile (PersistentProjectileEntity projectile) {
        if (projectile instanceof ILoyalty) {
            return projectile;
        }
        return null;
    }

    public static void tick(PersistentProjectileEntity projectile) {
        ILoyalty loyaltyProjectile = (ILoyalty)getProjectile(projectile);
        Entity entity = projectile.getOwner();
        int i = getProjectile(projectile).getDataTracker().get(loyaltyProjectile.getLoyalty());
        if (i > 0 && (loyaltyProjectile.getDealtDamage() || projectile.isNoClip()) && entity != null) {
            if (projectile instanceof IRicochet && ((IRicochet)getProjectile(projectile)).getBlockHit()) {
                loyaltyProjectile.setInGroundTime(loyaltyProjectile.getInGroundTime() + 1);
            }

            if (!projectile.getWorld().isClient() && shouldReturn(projectile)){
                if (!isOwnerAlive(projectile)) {
                    if (!projectile.getWorld().isClient && projectile.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                        projectile.dropStack(loyaltyProjectile.asItemStack(), 0.1F);
                    }
                    projectile.discard();
                } else {
                    projectile.setNoClip(true);
                    Vec3d vec3d = entity.getEyePos().subtract(projectile.getPos());
                    projectile.setPos(projectile.getX(), projectile.getY() + vec3d.y * 0.015 * (double) i, projectile.getZ());
                    if (projectile.getWorld().isClient) {
                        projectile.lastRenderY = projectile.getY();
                    }

                    double d = 0.05 * (double) i;
                    projectile.setVelocity(projectile.getVelocity().multiply(0.95).add(vec3d.normalize().multiply(d)));
                    if (loyaltyProjectile.getReturnTimer() == 0) {
                        projectile.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                    }

                    loyaltyProjectile.setReturnTimer(loyaltyProjectile.getReturnTimer() + 1);
                }
            }
        }
    }

    public static boolean isOwnerAlive(PersistentProjectileEntity projectile) {
        Entity entity = getProjectile(projectile).getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    public static boolean shouldReturn(PersistentProjectileEntity projectile) {
        ILoyalty loyaltyProjectile = (ILoyalty)getProjectile(projectile);
        int i = getProjectile(projectile).getDataTracker().get(loyaltyProjectile.getLoyalty());
        if (i > 0) {
            loyaltyProjectile.setWait(loyaltyProjectile.getWait() + 1);
            int delay = 90 / i;
            if (projectile instanceof IRicochet && ((IRicochet) projectile).getRemainingBounces() == 0) return true;
            else if (projectile instanceof IRicochet && !((IRicochet) projectile).getBlockHit()) {
                return loyaltyProjectile.getWait() > delay + 30 * i;
            } else return loyaltyProjectile.getWait() > delay;
        } else return false;
    }

}
