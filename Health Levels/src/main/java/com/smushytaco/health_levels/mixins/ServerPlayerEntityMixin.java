package com.smushytaco.health_levels.mixins;
import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }
    @Inject(method = "moveToWorld", at = @At(value = "RETURN", ordinal = 1))
    private void hookMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        HealthMethods.INSTANCE.onModified(this);
        ((GetEntryAccessor) dataTracker).invokeGetEntry(HealthAccessor.getHEALTH()).setDirty(true);
    }
    @Inject(method = "writeCustomDataToTag", at = @At("HEAD"))
    private void hookWriteCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        tag.put(HealthLevels.MOD_ID, HealthMethods.INSTANCE.getTag(this));
    }
    @Inject(method = "readCustomDataFromTag", at = @At("HEAD"))
    private void hookReadCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        Tag data = tag.get(HealthLevels.MOD_ID);
        if (data == null) return;
        HealthMethods.INSTANCE.readFromTag(this, data);
    }
}