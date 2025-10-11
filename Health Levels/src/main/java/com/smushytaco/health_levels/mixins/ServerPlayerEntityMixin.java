package com.smushytaco.health_levels.mixins;
import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    protected ServerPlayerEntityMixin(World world, GameProfile profile) { super(world, profile); }
    @Inject(method = "teleportTo*", at = @At(value = "RETURN", ordinal = 1))
    private void hookTeleportTo(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        HealthMethods.INSTANCE.onModified(this, false);
        ((GetEntryAccessor) dataTracker).invokeGetEntry(HealthAccessor.getHEALTH()).setDirty(true);
    }
    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void hookWriteCustomData(WriteView view, CallbackInfo ci) { view.put(HealthLevels.MOD_ID, NbtCompound.CODEC, HealthMethods.INSTANCE.getTag(this)); }
    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void hookReadCustomData(ReadView view, CallbackInfo ci) {
        Optional<NbtCompound> data = view.read(HealthLevels.MOD_ID, NbtCompound.CODEC);
        if (data.isEmpty()) return;
        HealthMethods.INSTANCE.readFromTag(this, data.get());
    }
}