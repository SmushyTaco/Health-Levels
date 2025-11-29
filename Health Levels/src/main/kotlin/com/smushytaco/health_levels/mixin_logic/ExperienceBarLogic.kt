package com.smushytaco.health_levels.mixin_logic
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.HealthLevelsClient
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.profiling.Profiler
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
object ExperienceBarLogic {
    private val ICONS = "textures/gui/experience_bars.png".identifier
    fun hookRenderExperienceBarLogic(client: Minecraft, ci: CallbackInfo, context: GuiGraphics, x: Int, textRenderer: Font) {
        if (!HealthLevels.config.enableHealthExperienceBar) return
        val player = client.player ?: return
        if (player !is HealthLevelsXP) return
        ci.cancel()
        Profiler.get().push("levelUpHPBars")
        run {
            val target = HealthLevels.config.levelsAndXP[player.healthLevel.coerceAtMost(HealthLevels.config.levelsAndXP.size - 1)]
            val hpXpBarWidth = if (target != 0) HealthLevelsClient.healthXP * 91 / target else 0
            val mcXpBarWidth = (player.experienceProgress * 91).toInt()
            val top = context.guiHeight() - 29
            renderProgress(ICONS, context, x, top, 0.0F, hpXpBarWidth)
            renderProgress(ICONS, context, x + 91, top, 91.0F, mcXpBarWidth)
        }
        Profiler.get().pop()
        Profiler.get().push("levelUpHPLevels")
        run {
            val hpLevel = HealthLevelsClient.healthLevel
            val mcLevel = player.experienceLevel
            val centerX = context.guiWidth() / 2
            renderLevel(context.guiHeight(), textRenderer, context, hpLevel, centerX - 92, -49345, true)
            renderLevel(context.guiHeight(), textRenderer, context, mcLevel, centerX + 93, -8323296, false)
        }
        Profiler.get().pop()
    }
    @Suppress("SameParameterValue")
    private fun renderProgress(texture: ResourceLocation, context: GuiGraphics, left: Int, top: Int, texX: Float, filled: Int) {
        context.blit(RenderPipelines.GUI_TEXTURED, texture, left, top, texX, 0.0F, 91, 5, 256, 256)
        if (filled > 0) context.blit(RenderPipelines.GUI_TEXTURED, texture, left, top, texX, 5.0F, filled, 5, 256, 256)
    }
    private fun renderLevel(scaledHeight: Int, textRenderer: Font, context: GuiGraphics, level: Int, left: Int, color: Int, isHealthLevel: Boolean) {
        if (level == 0) return
        val text = Component.translatable("gui.experience.level", level)
        val updatedLeft = if (isHealthLevel) left - textRenderer.width(text) else left
        val top = scaledHeight - 30
        context.drawString(textRenderer, text, updatedLeft + 1, top, -16777216, false)
        context.drawString(textRenderer, text, updatedLeft - 1, top, -16777216, false)
        context.drawString(textRenderer, text, updatedLeft, top + 1, -16777216, false)
        context.drawString(textRenderer, text, updatedLeft, top - 1, -16777216, false)
        context.drawString(textRenderer, text, updatedLeft, top, color, false)
    }
}