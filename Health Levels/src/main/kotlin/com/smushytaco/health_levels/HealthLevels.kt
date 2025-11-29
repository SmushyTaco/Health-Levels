package com.smushytaco.health_levels
import com.smushytaco.health_levels.abstractions.HealthMethods.copyPlayerData
import com.smushytaco.health_levels.abstractions.HealthMethods.deathPenalty
import com.smushytaco.health_levels.abstractions.HealthMethods.onModified
import com.smushytaco.health_levels.command.Command
import com.smushytaco.health_levels.configuration_support.ModConfig
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.XpPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
object HealthLevels : ModInitializer {
    const val MOD_ID = "health_levels"
    val HEALTH_MODIFIER_IDENTIFIER = "health_modifier".identifier
    val config = ModConfig.createAndLoad()
    private val LEVEL_UP_HEALTH_IDENTIFIER = "level_up_health".identifier
    val LEVEL_UP_HEALTH: SoundEvent = SoundEvent.createVariableRangeEvent(LEVEL_UP_HEALTH_IDENTIFIER)
    override fun onInitialize() {
        PayloadTypeRegistry.playS2C().register(XpPayload.payloadId, XpPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(LevelPayload.payloadId, LevelPayload.CODEC)
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { it.commands.dispatcher.register(Command.buildHealthLevelsCommand()) })
        ServerPlayerEvents.AFTER_RESPAWN.register(ServerPlayerEvents.AfterRespawn { _, newPlayer, _ ->
            newPlayer.onModified()
            newPlayer.health = newPlayer.maxHealth
        })
        ServerPlayerEvents.COPY_FROM.register(ServerPlayerEvents.CopyFrom { oldPlayer, newPlayer, alive ->
            newPlayer.copyPlayerData(oldPlayer)
            if (!alive) newPlayer.deathPenalty()
        })
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler, _, _ -> handler.player.onModified() })
    }
    val String.identifier: ResourceLocation
        get() = ResourceLocation.fromNamespaceAndPath(MOD_ID, this)
}