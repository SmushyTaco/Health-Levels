package com.smushytaco.health_levels.mixin_logic
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.HealthLevelsClient
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import net.minecraft.util.profiler.Profilers
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
object InGameHudLogic {
    private val ICONS = "textures/gui/experience_bars.png".identifier
    fun hookRenderExperienceBarLogic(client: MinecraftClient, ci: CallbackInfo, context: DrawContext, x: Int, fontRenderer: TextRenderer) {
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
            val hpLevel = HealthLevelsClient.healthLevel.toString()
            val mcLevel = player.experienceLevel.toString()
            val centerX = context.scaledWindowWidth / 2
            val hpLevelWidth = fontRenderer.getWidth(hpLevel)
            renderLevel(context.scaledWindowHeight, fontRenderer, context, hpLevel, centerX - 92 - hpLevelWidth, 0xff3f3f)
            renderLevel(context.scaledWindowHeight, fontRenderer, context, mcLevel, centerX + 93, 0x80FF20)
        }
        Profilers.get().pop()
    }
    @Suppress("SameParameterValue")
    private fun renderProgress(texture: Identifier, context: DrawContext, left: Int, top: Int, texX: Float, filled: Int) {
        context.drawTexture(RenderLayer::getGuiTextured, texture, left, top, texX, 0.0F, 91, 5, 256, 256)
        if (filled > 0) context.drawTexture(RenderLayer::getGuiTextured, texture, left, top, texX, 5.0F, filled, 5, 256, 256)
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