package com.smushytaco.health_levels.mixins;
import com.mojang.authlib.GameProfile;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthLevelsXP;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Player.class)
public abstract class PlayerEntityMixin implements HealthLevelsXP {
    @Unique
    private int healthLevel;
    @Unique
    private int healthXP;
    @Unique
    private boolean hasLeveledUp;
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public int getHealthLevel() { return Mth.clamp(healthLevel, 0, HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size()); }
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setHealthLevel(int healthLevel) {
        int previousHealthLevel = getHealthLevel();
        this.healthLevel = Mth.clamp(healthLevel, 0, HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size());
        if (getHealthLevel() > previousHealthLevel) { setHasLeveledUp(true); }
    }
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public int getHealthXP() {
        if (getHealthLevel() == HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size() || healthXP < 0) setHealthXP(0);
        return healthXP;
    }
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setHealthXP(int healthXP) {
        if (getHealthLevel() == HealthLevels.INSTANCE.getConfig().getLevelsAndXP().size() || healthXP < 0) healthXP = 0;
        this.healthXP = healthXP;
    }
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public boolean getHasLeveledUp() { return hasLeveledUp; }
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setHasLeveledUp(boolean hasLeveledUp) { this.hasLeveledUp = hasLeveledUp; }
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Level world, GameProfile profile, CallbackInfo ci) { HealthMethods.INSTANCE.updateHealth((Player) (Object) this); }
    @Inject(method = "giveExperiencePoints", at = @At("HEAD"))
    private void hookAddExperience(int experience, CallbackInfo ci) {
        setHealthXP(getHealthXP() + experience);
        HealthMethods.INSTANCE.onModified((Player) (Object) this, false);
    }
}