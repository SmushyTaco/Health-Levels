package com.smushytaco.health_levels.mixin_logic
import com.smushytaco.health_levels.HealthLevels
import com.smushytaco.health_levels.HealthLevels.identifier
import com.smushytaco.health_levels.HealthLevelsClient
import com.smushytaco.health_levels.abstractions.HealthLevelsXP
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
@Environment(EnvType.CLIENT)
object InGameHudLogic {
    private val ICONS = "textures/gui/icons.png".identifier
    fun DrawableHelper.hookRenderExperienceBarLogic(client: MinecraftClient, ci: CallbackInfo, scaledHeight: Int, matrices: MatrixStack, x: Int, scaledWidth: Int, fontRenderer: TextRenderer) {
        if (!HealthLevels.config.enableHealthExperienceBar) return
        val player = client.player ?: return
        if (player !is HealthLevelsXP) return
        ci.cancel()
        client.textureManager.bindTexture(ICONS)
        client.profiler.push("levelUpHPBars")
        run {
            val target = HealthLevelsClient.levelsAndXP[player.healthLevel.coerceAtMost(HealthLevelsClient.levelsAndXP.size - 1)]
            val hpXpBarWidth = if (target != 0) player.healthXP * 91 / target else 0
            val mcXpBarWidth = (player.experienceProgress * 91).toInt()
            val top = scaledHeight - 29
            renderProgress(matrices, x, top, 0, hpXpBarWidth)
            renderProgress(matrices, x + 91, top, 91, mcXpBarWidth)
        }
        client.profiler.pop()
        client.profiler.push("levelUpHPLevels")
        run {
            val hpLevel = HealthLevelsClient.healthLevel.toString()
            val mcLevel = player.experienceLevel.toString()
            val centerX = scaledWidth / 2
            val hpLevelWidth = fontRenderer.getWidth(hpLevel)
            renderLevel(scaledHeight, fontRenderer, matrices, hpLevel, centerX - 92 - hpLevelWidth, 0xff3f3f)
            renderLevel(scaledHeight, fontRenderer, matrices, mcLevel, centerX + 93, 0x80FF20)
        }
        client.profiler.pop()
        client.textureManager.bindTexture(DrawableHelper.GUI_ICONS_TEXTURE)
    }
    private fun DrawableHelper.renderProgress(matrices: MatrixStack, left: Int, top: Int, texX: Int, filled: Int) {
        drawTexture(matrices, left, top, texX, 0, 91, 5)
        if (filled > 0) {
            drawTexture(matrices, left, top, texX, 5, filled, 5)
        }
    }
    private fun renderLevel(scaledHeight: Int, fontRenderer: TextRenderer, matrices: MatrixStack, str: String, left: Int, color: Int) {
        val top = scaledHeight - 30
        fontRenderer.draw(matrices, str, (left + 1).toFloat(), top.toFloat(), 0)
        fontRenderer.draw(matrices, str, (left - 1).toFloat(), top.toFloat(), 0)
        fontRenderer.draw(matrices, str, left.toFloat(), (top + 1).toFloat(), 0)
        fontRenderer.draw(matrices, str, left.toFloat(), (top - 1).toFloat(), 0)
        fontRenderer.draw(matrices, str, left.toFloat(), top.toFloat(), color)
    }
}