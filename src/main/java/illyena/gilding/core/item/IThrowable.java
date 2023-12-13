package illyena.gilding.core.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IThrowable {

    public abstract PersistentProjectileEntity getProjectileEntity(World world, PlayerEntity playerEntity, ItemStack stack);

    public abstract float getPullProgress(int useTicks);

    public default void onThrow(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity && canThrow(stack, world, user, remainingUseTicks)) {
            int i = stack.getItem().getMaxUseTime(stack) - remainingUseTicks;
            if (!((double)getPullProgress(i) < 0.1)) {
                int j = getRiptide(stack, world, user, remainingUseTicks);
                    if (!world.isClient  && j <= 0) {
                        stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(user.getActiveHand()));
                        if (j == 0) {
                            PersistentProjectileEntity projectile = getProjectileEntity(world, (PlayerEntity) user, stack);
                            projectile.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F + (float) j * 0.5F, 0.0f); //1.0F);

                            if (getPullProgress(i) == 1.0f) {
                                projectile.setCritical(true);
                            }

                            if (EnchantmentHelper.getLevel(Enchantments.POWER, stack) > 0) {
                                projectile.setDamage(projectile.getDamage() + (double)EnchantmentHelper.getLevel(Enchantments.POWER, stack) * 0.5 +0.5);
                            }

                            if (EnchantmentHelper.getLevel(Enchantments.PUNCH, stack) > 0) {
                                projectile.setPunch(EnchantmentHelper.getLevel(Enchantments.PUNCH, stack));
                            }

                            if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                                projectile.setOnFireFor(100);
                            }

                            if (playerEntity.getAbilities().creativeMode) {
                                projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }
                            world.spawnEntity(projectile);
                            world.playSoundFromEntity((PlayerEntity) null, projectile, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F); //todo sound
                            if (!playerEntity.getAbilities().creativeMode) {
                                playerEntity.getInventory().removeOne(stack);
                            }
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                    if (j > 0) {
                        this.riptide(stack, world, playerEntity, remainingUseTicks);
                    }
            }
        }
    }

    public default void riptide(ItemStack stack, World world, PlayerEntity playerEntity, int remainingUseTicks) {
        int riptide = this.getRiptide(stack, world, playerEntity, remainingUseTicks);
        float f = playerEntity.getYaw();
        float g = playerEntity.getPitch();
        float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
        float k = -MathHelper.sin(g * 0.017453292F);
        float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 3.0F * ((1.0F + (float) riptide) / 4.0F);
        h *= n / m;
        k *= n / m;
        l *= n / m;
        playerEntity.addVelocity(h, k, l);
        playerEntity.useRiptide(20);
        if (playerEntity.isOnGround()) {
            float o = 1.1999999F;
            playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263, 0.0));
        }

        SoundEvent soundEvent;
        if (riptide >= 3) {
            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
        } else if (riptide == 2) {
            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
        } else {
            soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
        }

        world.playSoundFromEntity(null, playerEntity, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    public abstract boolean canThrow(ItemStack stack, World world, LivingEntity user, int remainingTicks);

    public default int getRiptide(ItemStack stack, World world, LivingEntity user, int remainingTicks) {
        return user.isTouchingWaterOrRain() ? EnchantmentHelper.getRiptide(stack) : 0;
    }

}