package com.smushytaco.health_levels.mixins;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(LivingEntity.class)
public interface HealthAccessor {
    @Accessor
    static EntityDataAccessor<Float> getDATA_HEALTH_ID() { throw new AssertionError(); }
}