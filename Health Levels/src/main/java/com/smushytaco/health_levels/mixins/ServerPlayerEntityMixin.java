package com.smushytaco.health_levels.mixins;
import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {
    protected ServerPlayerEntityMixin(Level world, GameProfile profile) { super(world, profile); }
    @Inject(method = "teleport*", at = @At(value = "RETURN", ordinal = 1))
    private void hookTeleportTo(TeleportTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        HealthMethods.INSTANCE.onModified(this, false);
        ((GetEntryAccessor) entityData).invokeGetItem(HealthAccessor.getDATA_HEALTH_ID()).setDirty(true);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void hookWriteCustomData(ValueOutput view, CallbackInfo ci) { view.store(HealthLevels.MOD_ID, CompoundTag.CODEC, HealthMethods.INSTANCE.getTag(this)); }
    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void hookReadCustomData(ValueInput view, CallbackInfo ci) {
        Optional<CompoundTag> data = view.read(HealthLevels.MOD_ID, CompoundTag.CODEC);
        if (data.isEmpty()) return;
        HealthMethods.INSTANCE.readFromTag(this, data.get());
    }
}