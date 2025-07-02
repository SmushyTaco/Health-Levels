package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.abstractions.HealthLevelsXP;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Bar.class)
public interface BarMixin {
    @Inject(method = "drawExperienceLevel", at = @At("HEAD"), cancellable = true)
    private static void hookDrawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level, CallbackInfo ci) {
        if (!HealthLevels.INSTANCE.getConfig().getEnableHealthExperienceBar()) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!(player instanceof HealthLevelsXP)) return;
        ci.cancel();
    }
}