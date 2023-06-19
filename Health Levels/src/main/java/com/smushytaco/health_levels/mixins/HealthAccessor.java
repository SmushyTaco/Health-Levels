package com.smushytaco.health_levels.mixins;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(LivingEntity.class)
public interface HealthAccessor {
    @Accessor
    static TrackedData<Float> getHEALTH() { throw new AssertionError(); }
}