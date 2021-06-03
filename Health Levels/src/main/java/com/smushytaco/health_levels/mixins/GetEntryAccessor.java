package com.smushytaco.health_levels.mixins;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(DataTracker.class)
public interface GetEntryAccessor {
    @Invoker
    <T> DataTracker.Entry<T> invokeGetEntry(TrackedData<T> trackedData);
}