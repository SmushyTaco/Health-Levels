package com.smushytaco.health_levels.mixins;
import com.smushytaco.health_levels.abstractions.HealthMethods;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void hookOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        HealthMethods.INSTANCE.onModified(player);
    }
}