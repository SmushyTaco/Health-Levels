package com.smushytaco.health_levels.mixins;
import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthLevelsXP;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements HealthLevelsXP {
    @Unique
    private int healthLevel;
    @Unique
    private int healthXP;
    @Unique
    private boolean hasLeveledUp;
    @Override
    public int getHealthLevel() { return MathHelper.clamp(healthLevel, 0, HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size()); }
    @Override
    public void setHealthLevel(int healthLevel) {
        int previousHealthLevel = getHealthLevel();
        this.healthLevel = MathHelper.clamp(healthLevel, 0, HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size());
        if (getHealthLevel() > previousHealthLevel) { setHasLeveledUp(true); }
    }
    @Override
    public int getHealthXP() {
        if (getHealthLevel() == HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size() || healthXP < 0) setHealthXP(0);
        return healthXP;
    }
    @Override
    public void setHealthXP(int healthXP) {
        if (getHealthLevel() == HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size() || healthXP < 0) healthXP = 0;
        this.healthXP = healthXP;
    }
    @Override
    public boolean getHasLeveledUp() { return hasLeveledUp; }
    @Override
    public void setHasLeveledUp(boolean hasLeveledUp) { this.hasLeveledUp = hasLeveledUp; }
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) { HealthMethods.INSTANCE.updateHealth((PlayerEntity) (Object) this); }
    @Inject(method = "addExperience", at = @At("HEAD"))
    private void hookAddExperience(int experience, CallbackInfo ci) {
        setHealthXP(getHealthXP() + experience);
        HealthMethods.INSTANCE.onModified((PlayerEntity) (Object) this);
    }
}