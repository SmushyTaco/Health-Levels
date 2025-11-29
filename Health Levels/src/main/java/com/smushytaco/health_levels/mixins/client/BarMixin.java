package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthLevelsXP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ContextualBarRenderer.class)
public interface BarMixin {
    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void hookDrawExperienceLevel(GuiGraphics context, Font textRenderer, int level, CallbackInfo ci) {
        if (!HealthLevels.INSTANCE.getConfig().getEnableHealthExperienceBar()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!(player instanceof HealthLevelsXP)) return;
        ci.cancel();
    }
}