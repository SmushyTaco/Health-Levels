package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.HealthLevels;
import com.smushytaco.health_levels.mixin_logic.InGameHudLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    public abstract TextRenderer getTextRenderer();
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void hookRenderExperienceBar(DrawContext context, int x, CallbackInfo ci) { InGameHudLogic.INSTANCE.hookRenderExperienceBarLogic(client, ci, context, x, getTextRenderer()); }
    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void hookRenderExperienceLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) { if (HealthLevels.INSTANCE.getConfig().getEnableHealthExperienceBar()) ci.cancel(); }
}