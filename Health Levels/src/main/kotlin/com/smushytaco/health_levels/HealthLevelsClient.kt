package com.smushytaco.health_levels
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import com.smushytaco.health_levels.payloads.LevelsAndXpPayload
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.XpPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
@Suppress("UNUSED")
@Environment(EnvType.CLIENT)
object HealthLevelsClient : ClientModInitializer {
    var levelsAndXP = listOf(0)
        private set
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
        ClientPlayNetworking.registerGlobalReceiver(LevelsAndXpPayload.payloadId) { levelsAndXpPayload, _ -> levelsAndXP = levelsAndXpPayload.value }
    }
}