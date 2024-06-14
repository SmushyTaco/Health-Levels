package com.smushytaco.health_levels.abstractions
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.HEALTH_MODIFIER_IDENTIFIER
import com.smushytaco.health_levels.HealthLevels.config
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.configuration_support.LoseType
import com.smushytaco.health_levels.payloads.LevelsAndXpPayload
import com.smushytaco.health_levels.payloads.LevelPayload
import com.smushytaco.health_levels.payloads.XpPayload
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
object HealthMethods {
    val HEALTH_XP_PACKET_IDENTIFIER = "health_xp".identifier
    val HEALTH_LEVEL_PACKET_IDENTIFIER = "health_level".identifier
    val CONFIG_PACKET_IDENTIFIER = "config".identifier
    private const val HEALTH_XP_KEY = "healthXP"
    private const val HEALTH_LEVEL_KEY = "healthLevel"
    fun PlayerEntity.updateHealth() {
        if (this !is ServerPlayerEntity || this !is HealthLevelsXP) return
        if (networkHandler != null) {
            ServerPlayNetworking.send(this, XpPayload(healthXP))
            ServerPlayNetworking.send(this, LevelPayload(healthLevel))
            ServerPlayNetworking.send(this, LevelsAndXpPayload(config.levelsAndXP))
        }
        val entityAttributeModifier = EntityAttributeModifier(HEALTH_MODIFIER_IDENTIFIER, (-20 + config.startingHP + healthLevel * config.hpPerLevel).coerceAtLeast(-19).toDouble(), EntityAttributeModifier.Operation.ADD_VALUE)
        val entityAttributeInstance = getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
        entityAttributeInstance?.removeModifier(entityAttributeModifier)
        entityAttributeInstance?.addPersistentModifier(entityAttributeModifier)
        if (health > maxHealth || config.healOnLevelUp && hasLeveledUp) health = maxHealth
        if (hasLeveledUp) {
            hasLeveledUp = false
            world.playSound(null, x, y, z, HealthLevels.LEVEL_UP_HEALTH, SoundCategory.PLAYERS, 1.0F, 1.0F)
        }
    }
    fun PlayerEntity.copyPlayerData(playerEntity: PlayerEntity) {
        if (this !is HealthLevelsXP || playerEntity !is HealthLevelsXP) return
        healthLevel = playerEntity.healthLevel
        healthXP = playerEntity.healthXP
        onModified()
    }
    fun PlayerEntity.onModified() {
        if (this !is HealthLevelsXP) return
        while(healthLevel < config.levelsAndXP.size && healthXP >= config.levelsAndXP[healthLevel.coerceAtMost(config.levelsAndXP.size - 1)]) {
            healthXP -= config.levelsAndXP[healthLevel.coerceAtMost(config.levelsAndXP.size - 1)]
            healthLevel++
        }
        updateHealth()
    }
    fun PlayerEntity.deathPenalty() {
        if (this !is HealthLevelsXP) return
        if (config.loseType == LoseType.XP || config.loseType == LoseType.LEVELS_AND_XP) healthXP = 0
        if (config.loseType == LoseType.LEVELS_AND_XP) healthLevel = 0
        onModified()
    }
    val PlayerEntity.tag: NbtCompound
        get() {
            val nbtCompound = NbtCompound()
            if (this !is HealthLevelsXP) return nbtCompound
            nbtCompound.putInt(HEALTH_LEVEL_KEY, healthLevel)
            nbtCompound.putInt(HEALTH_XP_KEY, healthXP)
            return nbtCompound
        }
    fun PlayerEntity.readFromTag(nbt: NbtElement) {
        nbt as NbtCompound
        if (this !is HealthLevelsXP) return
        healthLevel = nbt.getInt(HEALTH_LEVEL_KEY)
        healthXP = nbt.getInt(HEALTH_XP_KEY)
        onModified()
    }
}