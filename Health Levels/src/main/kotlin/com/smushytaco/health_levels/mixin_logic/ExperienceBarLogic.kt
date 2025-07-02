package com.smushytaco.health_levels.mixin_logic
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.HealthLevelsClient
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profilers
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
object ExperienceBarLogic {
    private val ICONS = "textures/gui/experience_bars.png".identifier
    fun hookRenderExperienceBarLogic(client: MinecraftClient, ci: CallbackInfo, context: DrawContext, x: Int, textRenderer: TextRenderer) {
        if (!HealthLevels.config.enableHealthExperienceBar) return
        val player = client.player ?: return
        if (player !is HealthLevelsXP) return
        ci.cancel()
        Profilers.get().push("levelUpHPBars")
        run {
            val target = HealthLevels.config.levelsAndXP[player.healthLevel.coerceAtMost(HealthLevels.config.levelsAndXP.size - 1)]
            val hpXpBarWidth = if (target != 0) HealthLevelsClient.healthXP * 91 / target else 0
            val mcXpBarWidth = (player.experienceProgress * 91).toInt()
            val top = context.scaledWindowHeight - 29
            renderProgress(ICONS, context, x, top, 0.0F, hpXpBarWidth)
            renderProgress(ICONS, context, x + 91, top, 91.0F, mcXpBarWidth)
        }
        Profilers.get().pop()
        Profilers.get().push("levelUpHPLevels")
        run {
            val hpLevel = HealthLevelsClient.healthLevel
            val mcLevel = player.experienceLevel
            val centerX = context.scaledWindowWidth / 2
            renderLevel(context.scaledWindowHeight, textRenderer, context, hpLevel, centerX - 92, -49345, true)
            renderLevel(context.scaledWindowHeight, textRenderer, context, mcLevel, centerX + 93, -8323296, false)
        }
        Profilers.get().pop()
    }
    @Suppress("SameParameterValue")
    private fun renderProgress(texture: Identifier, context: DrawContext, left: Int, top: Int, texX: Float, filled: Int) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, left, top, texX, 0.0F, 91, 5, 256, 256)
        if (filled > 0) context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, left, top, texX, 5.0F, filled, 5, 256, 256)
    }
    private fun renderLevel(scaledHeight: Int, textRenderer: TextRenderer, context: DrawContext, level: Int, left: Int, color: Int, isHealthLevel: Boolean) {
        if (level == 0) return
        val text = Text.translatable("gui.experience.level", level)
        val updatedLeft = if (isHealthLevel) left - textRenderer.getWidth(text) else left
        val top = scaledHeight - 30
        context.drawText(textRenderer, text, updatedLeft + 1, top, -16777216, false)
        context.drawText(textRenderer, text, updatedLeft - 1, top, -16777216, false)
        context.drawText(textRenderer, text, updatedLeft, top + 1, -16777216, false)
        context.drawText(textRenderer, text, updatedLeft, top - 1, -16777216, false)
        context.drawText(textRenderer, text, updatedLeft, top, color, false)
    }
}