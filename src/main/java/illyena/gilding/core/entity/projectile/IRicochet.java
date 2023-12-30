package illyena.gilding.core.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

/** <pre>
 * INSERT
 *
 * insert fields:
 *      {@code private static final TrackedData<Integer> RICOCHET;
 * private static final TrackedData<Boolean> ENCHANTED;
 *
 * private int bounces;
 * private List<Entity> ricochetHitEntities = Lists.newArrayListWithCapacity(bounces);
 * private int remainingBounces;}
 *
 * insert at init:
 *      {@code this.bounces = GildingEnchantmentHelper.getRicochet(this.capShieldStack) * 2;
 *          this.dataTracker.set(RICOCHET,
 *              (byte)GildingEnchantmentHelper.getRicochet(stack));
 *          this.dataTracker.set(LOYALTY, (byte) EnchantmentHelper.getLoyalty(stack));
 *          this.dataTracker.set(ENCHANTED, stack.hasGlint());}
 *
 * insert at #initDataTracker
 *          {@code this.dataTracker.startTracking(RICOCHET, (byte) 0);
 * this.dataTracker.startTracking(LOYALTY, (byte) 0);
 * this.dataTracker.startTracking(ENCHANTED, false);}
 *
 * insert at end of #onEntityHit:
 *      {@code if (IRicochet.nextRicochetTarget(this, (LivingEntity) entity)
 *                  != null && this.remainingBounces > 0) {
 *             IRicochet.ricochet(this, (LivingEntity) entity);
 *         } else {
 *             this.setNoGravity(false);
 *             this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
 *         }}
 * </pre>
 */
@SuppressWarnings({"UnnecessaryModifier", "unused"})
public interface IRicochet {

    public abstract double getRicochetRange();

    public abstract int getBounces();

    public abstract List<Entity> getRicochetHitEntities();

    public abstract int getRemainingBounces();

    public abstract void setRemainingBounces(int value);

    public abstract boolean getBlockHit();

    public abstract TrackedData<Integer> getRicochet();

    public abstract int getHangTime();

    public abstract void setHangTime(int value);
    
    public abstract ItemStack asItemStack();

    public static PersistentProjectileEntity getProjectile (PersistentProjectileEntity projectile) {
        if (projectile instanceof IRicochet) {
            return projectile;
        }
        return null;
    }

    public static void tick(PersistentProjectileEntity projectile) {
        IRicochet ricochetProjectile = (IRicochet)getProjectile(projectile);
        int i = projectile.getDataTracker().get(ricochetProjectile.getRicochet());
        if (i > 0 && ricochetProjectile.getRicochetHitEntities().size() > 0 && !projectile.isOnGround()) {
            ricochetProjectile.setHangTime(ricochetProjectile.getHangTime() + 1);
            if (ricochetProjectile.getHangTime() > 90) {
                projectile.setNoClip(false);
                projectile.setNoGravity(false);
                boolean bl = MathHelper.sign(projectile.getVelocity().getY()) == -1;
                projectile.setVelocity(projectile.getVelocity().multiply(-0.01, bl ? 1 : -1.0, -0.01));
            }
        }
    }

    public static void ricochet(PersistentProjectileEntity projectile, LivingEntity entity) {
        float k = 0.1f; //velocity multiplier
        LivingEntity nextTarget = nextRicochetTarget(projectile, entity);
        Vec3d nextTargetPos = nextTarget.getPos();
        Vec3d vec3d = new Vec3d(nextTargetPos.x - projectile.getPos().x, nextTargetPos.y - projectile.getPos().y, nextTargetPos.z - projectile.getPos().z);
        vec3d = vec3d.multiply(k);
        projectile.setVelocity(vec3d);
        projectile.setNoGravity(true);
    }

    public static LivingEntity nextRicochetTarget(PersistentProjectileEntity projectile, LivingEntity livingEntity) {
        return projectile.getWorld().getClosestEntity(LivingEntity.class, TargetPredicate.createAttackable().setPredicate(Predicate.not(Predicate.isEqual(projectile.getOwner()))), livingEntity,
                livingEntity.getPos().x, livingEntity.getPos().y, livingEntity.getPos().z, livingEntity.getBoundingBox().expand(((IRicochet)getProjectile(projectile)).getRicochetRange()));
    }

    public static void onEntityHit (PersistentProjectileEntity projectile, Entity entity) {
        IRicochet ricochetProjectile = (IRicochet)getProjectile(projectile);
        ricochetProjectile.handleStackDamage(projectile);
        ricochetProjectile.getRicochetHitEntities().add(entity);
        ricochetProjectile.setRemainingBounces(ricochetProjectile.getBounces() - ricochetProjectile.getRicochetHitEntities().size());
        if (IRicochet.nextRicochetTarget(projectile, (LivingEntity) entity) != null && ricochetProjectile.getRemainingBounces() > 0) {
            IRicochet.ricochet(projectile, (LivingEntity) entity);
        } else {
            projectile.setNoGravity(false);
            projectile.setVelocity(projectile.getVelocity().multiply(-0.01, -0.1, -0.01));
        }
    }

    public static void onBlockHit(PersistentProjectileEntity projectile, BlockHitResult blockHitResult) {
        ((IRicochet)getProjectile(projectile)).setHangTime(0);
    }

    default void handleStackDamage(PersistentProjectileEntity projectile) {
        if (projectile.getWorld() instanceof ServerWorld world && ((IRicochet)projectile).getRicochetHitEntities().size() >= 1) {
            ((IRicochet)projectile).asItemStack().damage(1, world.random, projectile.getOwner() instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null);
        }
    }

}
