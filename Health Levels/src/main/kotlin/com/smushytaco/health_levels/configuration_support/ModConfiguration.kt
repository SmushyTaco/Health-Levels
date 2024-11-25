package com.smushytaco.health_levels.configuration_support
import com.smushytaco.health_levels.HealthLevels
import io.wispforest.owo.config.Option
import io.wispforest.owo.config.annotation.Config
import io.wispforest.owo.config.annotation.Modmenu
import io.wispforest.owo.config.annotation.Sync
@Modmenu(modId = HealthLevels.MOD_ID)
@Config(name = HealthLevels.MOD_ID, wrapperName = "ModConfig")
@Suppress("UNUSED")
class ModConfiguration {
    @JvmField
    var enableHealthExperienceBar = true
    @JvmField
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    var levelsAndXP = listOf(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000)
    @JvmField
    var hpPerLevel = 2
    @JvmField
    var startingHP = 10
    @JvmField
    var healOnLevelUp = true
    @JvmField
    var loseType = LoseType.XP
}