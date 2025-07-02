package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.mixin_logic.ExperienceBarLogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ExperienceBar.class)
public abstract class ExperienceBarMixin implements Bar {
    @Shadow
    @Final
    private MinecraftClient client;
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    private void hookRenderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ExperienceBarLogic.INSTANCE.hookRenderExperienceBarLogic(client, ci, context, getCenterX(client.getWindow()), client.textRenderer);
    }
}