package illyena.gilding.mixin.block.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookshelfBlockEntity.class)
public abstract class ChiseledBookshelfBlockEntityMixin {
    @Mutable
    @Shadow @Final private DefaultedList<ItemStack> inventory;

    @Shadow public abstract int size();

    @Shadow protected abstract void updateState(int interactedSlot);

    private static final String LOOT_TABLE_KEY = "LootTable";
    private static final String LOOT_TABLE_SEED_KEY = "LootTableSeed";
    @Nullable
    private Identifier lootTableId;
    private long lootTableSeed;
    private ContainerLock lock;
    private Text customName;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.lock = ContainerLock.EMPTY;

    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        this.lock = ContainerLock.fromNbt(nbt);
        if (nbt.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
        this.updateState(0);
    }

    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        this.lock.writeNbt(nbt);
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    private boolean deserializeLootTable(NbtCompound nbt) {
        if (nbt.contains(LOOT_TABLE_KEY, 8)) {
            this.lootTableId = new Identifier(nbt.getString(LOOT_TABLE_KEY));
            this.lootTableSeed = nbt.getLong(LOOT_TABLE_SEED_KEY);
            return true;
        } else {
            return false;
        }
    }

    private boolean serializeLootTable(NbtCompound nbt) {
        if (this.lootTableId == null) {
            return false;
        } else {
            nbt.putString(LOOT_TABLE_KEY, this.lootTableId.toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong(LOOT_TABLE_SEED_KEY, this.lootTableSeed);
            }

            return true;
        }
    }

    private void checkLootInteraction(@Nullable PlayerEntity player) {
        if (this.lootTableId != null && ((ChiseledBookshelfBlockEntity)(Object)this).getWorld().getServer() != null) {
            LootTable lootTable = ((ChiseledBookshelfBlockEntity)(Object)this).getWorld().getServer().getLootManager().getLootTable(this.lootTableId);
            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, this.lootTableId);
            }

            this.lootTableId = null;
            LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld)((ChiseledBookshelfBlockEntity)(Object)this).getWorld())).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(((ChiseledBookshelfBlockEntity)(Object)this).getPos()));
            if (player != null) {
                builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
            }

            lootTable.supplyInventory((ChiseledBookshelfBlockEntity)(Object)this, builder.build(LootContextTypes.CHEST), this.lootTableSeed);
        }

    }

    @Inject(method = "getStack", at = @At("HEAD"))
    private void onGetStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        this.checkLootInteraction((PlayerEntity) null);
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"))
    private void onRemoveStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        this.checkLootInteraction((PlayerEntity) null);
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        this.checkLootInteraction((PlayerEntity) null);
    }

}
