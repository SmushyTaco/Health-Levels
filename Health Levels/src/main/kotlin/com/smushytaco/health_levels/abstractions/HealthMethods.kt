package com.smushytaco.health_levels.abstractions
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.HEALTH_MODIFIER_IDENTIFIER
import com.smushytaco.health_levels.HealthLevels.config
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.configuration_support.LoseType
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.XpPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import kotlin.jvm.optionals.getOrNull
object HealthMethods {
    val HEALTH_XP_PACKET_IDENTIFIER = "health_xp".identifier
    val HEALTH_LEVEL_PACKET_IDENTIFIER = "health_level".identifier
    private const val HEALTH_XP_KEY = "healthXP"
    private const val HEALTH_LEVEL_KEY = "healthLevel"
    fun Player.updateHealth() {
        if (this !is ServerPlayer || this !is HealthLevelsXP) return
        if (connection != null) {
            ServerPlayNetworking.send(this, XpPayload(healthXP))
            ServerPlayNetworking.send(this, LevelPayload(healthLevel))
        }
        val entityAttributeModifier = AttributeModifier(HEALTH_MODIFIER_IDENTIFIER, (-20 + config.startingHP + healthLevel * config.hpPerLevel).coerceAtLeast(-19).toDouble(), AttributeModifier.Operation.ADD_VALUE)
        val entityAttributeInstance = getAttribute(Attributes.MAX_HEALTH)
        entityAttributeInstance?.addOrReplacePermanentModifier(entityAttributeModifier)
        if (health > maxHealth || config.healOnLevelUp && hasLeveledUp) health = maxHealth
        if (hasLeveledUp) {
            hasLeveledUp = false
            playNotifySound(HealthLevels.LEVEL_UP_HEALTH, SoundSource.PLAYERS, 1.0F, 1.0F)
        }
    }
    fun Player.copyPlayerData(playerEntity: Player) {
        if (this !is HealthLevelsXP || playerEntity !is HealthLevelsXP) return
        healthLevel = playerEntity.healthLevel
        healthXP = playerEntity.healthXP
        onModified()
    }
    fun Player.onModified(isFromReadingNBT: Boolean = false) {
        if (this !is HealthLevelsXP) return
        while(healthLevel < config.levelsAndXP.size && healthXP >= config.levelsAndXP[healthLevel.coerceAtMost(config.levelsAndXP.size - 1)]) {
            healthXP -= config.levelsAndXP[healthLevel.coerceAtMost(config.levelsAndXP.size - 1)]
            healthLevel++
        }
        if (isFromReadingNBT) hasLeveledUp = false
        updateHealth()
    }
    fun Player.deathPenalty() {
        if (this !is HealthLevelsXP) return
        if (config.loseType == LoseType.XP || config.loseType == LoseType.LEVELS_AND_XP) healthXP = 0
        if (config.loseType == LoseType.LEVELS_AND_XP) healthLevel = 0
        onModified()
    }
    val Player.tag: CompoundTag
        get() {
            val nbtCompound = CompoundTag()
            if (this !is HealthLevelsXP) return nbtCompound
            nbtCompound.putInt(HEALTH_LEVEL_KEY, healthLevel)
            nbtCompound.putInt(HEALTH_XP_KEY, healthXP)
            return nbtCompound
        }
    fun Player.readFromTag(nbt: CompoundTag) {
        if (this !is HealthLevelsXP) return
        nbt.getInt(HEALTH_LEVEL_KEY).getOrNull()?.let { healthLevel = it }
        nbt.getInt(HEALTH_XP_KEY).getOrNull()?.let { healthXP = it }
        onModified(true)
    }
}