package com.smushytaco.health_levels
import com.smushytaco.health_levels.abstractions.HealthMethods.copyPlayerData
import com.smushytaco.health_levels.abstractions.HealthMethods.deathPenalty
import com.smushytaco.health_levels.abstractions.HealthMethods.onModified
import com.smushytaco.health_levels.command.Command
import com.smushytaco.health_levels.configuration_support.ModConfiguration
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.LevelsAndXpPayload
import com.smushytaco.health_levels.payloads.XpPayload
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import java.util.*
object HealthLevels : ModInitializer {
    const val MOD_ID = "health_levels"
    val HEALTH_MODIFIER_UUID: UUID = UUID.fromString("6a2fa460-2603-4a19-bdea-47c4036d6437")
    lateinit var config: ModConfiguration
        private set
    private val LEVEL_UP_HEALTH_IDENTIFIER = "level_up_health".identifier
    val LEVEL_UP_HEALTH: SoundEvent = SoundEvent.of(LEVEL_UP_HEALTH_IDENTIFIER)
    override fun onInitialize() {
        Registry.register(Registries.SOUND_EVENT, LEVEL_UP_HEALTH_IDENTIFIER, LEVEL_UP_HEALTH)
        AutoConfig.register(ModConfiguration::class.java) { definition: Config, configClass: Class<ModConfiguration> ->
            GsonConfigSerializer(definition, configClass)
        }
        config = AutoConfig.getConfigHolder(ModConfiguration::class.java).config
        PayloadTypeRegistry.playS2C().register(XpPayload.payloadId, XpPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(LevelPayload.payloadId, LevelPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(LevelsAndXpPayload.payloadId, LevelsAndXpPayload.CODEC)
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { it.commandManager.dispatcher.register(Command.buildHealthLevelsCommand()) })
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
    val String.identifier: Identifier
        get() = Identifier(MOD_ID, this)
}