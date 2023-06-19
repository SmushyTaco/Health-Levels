package com.smushytaco.health_levels.mixin_logic
import com.mojang.blaze3d.systems.RenderSystem
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.HealthLevelsClient
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
@Environment(EnvType.CLIENT)
object InGameHudLogic {
    private val ICONS = "textures/gui/experience_bars.png".identifier
    fun hookRenderExperienceBarLogic(texture: Identifier, client: MinecraftClient, ci: CallbackInfo, scaledHeight: Int, context: DrawContext, x: Int, scaledWidth: Int, fontRenderer: TextRenderer) {
        if (!HealthLevels.config.enableHealthExperienceBar) return
        val player = client.player ?: return
        if (player !is HealthLevelsXP) return
        ci.cancel()
        client.profiler.push("levelUpHPBars")
        run {
            val target = HealthLevelsClient.levelsAndXP[player.healthLevel.coerceAtMost(HealthLevelsClient.levelsAndXP.size - 1)]
            val hpXpBarWidth = if (target != 0) HealthLevelsClient.healthXP * 91 / target else 0
            val mcXpBarWidth = (player.experienceProgress * 91).toInt()
            val top = scaledHeight - 29
            renderProgress(ICONS, context, x, top, 0, hpXpBarWidth)
            renderProgress(ICONS, context, x + 91, top, 91, mcXpBarWidth)
        }
        client.profiler.pop()
        client.profiler.push("levelUpHPLevels")
        run {
            val hpLevel = HealthLevelsClient.healthLevel.toString()
            val mcLevel = player.experienceLevel.toString()
            val centerX = scaledWidth / 2
            val hpLevelWidth = fontRenderer.getWidth(hpLevel)
            renderLevel(scaledHeight, fontRenderer, context, hpLevel, centerX - 92 - hpLevelWidth, 0xff3f3f)
            renderLevel(scaledHeight, fontRenderer, context, mcLevel, centerX + 93, 0x80FF20)
        }
        client.profiler.pop()
        RenderSystem.setShaderTexture(0, texture)
    }
    @Suppress("SameParameterValue")
    private fun renderProgress(texture: Identifier, context: DrawContext, left: Int, top: Int, texX: Int, filled: Int) {
        context.drawTexture(texture, left, top, texX, 0, 91, 5)
        if (filled > 0) context.drawTexture(texture, left, top, texX, 5, filled, 5)
    }
    private fun renderLevel(scaledHeight: Int, fontRenderer: TextRenderer, context: DrawContext, str: String, left: Int, color: Int) {
        val top = scaledHeight - 30
        context.drawText(fontRenderer, str, (left + 1), top, 0, false)
        context.drawText(fontRenderer, str, (left - 1), top, 0, false)
        context.drawText(fontRenderer, str, left, (top + 1), 0, false)
        context.drawText(fontRenderer, str, left, (top - 1), 0, false)
        context.drawText(fontRenderer, str, left, top, color, false)
    }
}