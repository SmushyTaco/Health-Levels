package com.smushytaco.health_levels.mixins;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(SynchedEntityData.class)
public interface GetEntryAccessor {
    @Invoker
    <T> SynchedEntityData.DataItem<T> invokeGetItem(EntityDataAccessor<T> trackedData);
}