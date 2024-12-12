package com.smushytaco.health_levels
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.XpPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
object HealthLevelsClient : ClientModInitializer {
    var healthLevel = 0
        private set
    var healthXP = 0
        private set
    override fun onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(XpPayload.payloadId) { xpPayload, context ->
            this.healthXP = xpPayload.value
            context.player()?.let {
                if (it !is HealthLevelsXP) return@let
                it.healthXP = healthXP
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(LevelPayload.payloadId) { levelPayload, context ->
            this.healthLevel = levelPayload.value
            context.player()?.let {
                if (it !is HealthLevelsXP) return@let
                it.healthLevel = healthLevel
            }
        }
    }
}