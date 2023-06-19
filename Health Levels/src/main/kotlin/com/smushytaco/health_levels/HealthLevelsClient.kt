package com.smushytaco.health_levels
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import com.smushytaco.health_levels.abstractions.HealthMethods
import com.smushytaco.health_levels.configuration_support.ModConfiguration
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
        ClientPlayNetworking.registerGlobalReceiver(HealthMethods.HEALTH_XP_PACKET_IDENTIFIER) { client, _, buf, _ ->
            val healthXP = buf.readInt()
            this.healthXP = healthXP
            client.player?.let {
                if (it !is HealthLevelsXP) return@let
                it.healthXP = healthXP
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(HealthMethods.HEALTH_LEVEL_PACKET_IDENTIFIER) { client, _, buf, _ ->
            val healthLevel = buf.readInt()
            this.healthLevel = healthLevel
            client.player?.let {
                if (it !is HealthLevelsXP) return@let
                it.healthLevel = healthLevel
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(HealthMethods.CONFIG_PACKET_IDENTIFIER) { _, _, buf, _ ->
            val json = (1 .. buf.readInt()).fold(StringBuilder()) { s, _ -> s.append(buf.readChar()) }.toString()
            val config = HealthMethods.gson.fromJson(json, ModConfiguration::class.java)
            levelsAndXP = config.levelsAndXP
        }
    }
}