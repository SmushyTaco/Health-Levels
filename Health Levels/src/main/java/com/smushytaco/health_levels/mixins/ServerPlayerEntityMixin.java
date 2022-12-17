package com.smushytaco.health_levels.mixins;

import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }
    @Inject(method = "moveToWorld", at = @At(value = "RETURN", ordinal = 1))
    private void hookMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        HealthMethods.INSTANCE.onModified(this);
        ((GetEntryAccessor) dataTracker).invokeGetEntry(HealthAccessor.getHEALTH()).setDirty(true);
    }
    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void hookWriteCustomDataToTag(NbtCompound nbt, CallbackInfo ci) {
        nbt.put(HealthLevels.MOD_ID, HealthMethods.INSTANCE.getTag(this));
    }
    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void hookReadCustomDataFromTag(NbtCompound nbt, CallbackInfo ci) {
        NbtElement data = nbt.get(HealthLevels.MOD_ID);
        if (data == null) return;
        HealthMethods.INSTANCE.readFromTag(this, data);
    }
}