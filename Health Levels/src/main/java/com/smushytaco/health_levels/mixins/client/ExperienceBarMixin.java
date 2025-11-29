package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.mixin_logic.ExperienceBarLogic;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ExperienceBarRenderer.class)
public abstract class ExperienceBarMixin implements ContextualBarRenderer {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    private void hookRenderBar(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        ExperienceBarLogic.INSTANCE.hookRenderExperienceBarLogic(minecraft, ci, context, left(minecraft.getWindow()), minecraft.font);
    }
}