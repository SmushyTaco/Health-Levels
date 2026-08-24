package com.smushytaco.health_levels.mixins.client;
import com.smushytaco.health_levels.mixin_logic.ExperienceBarLogic;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ExperienceBar.class)
public abstract class ExperienceBarMixin implements ContextualBar {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
    private void hookRenderBar(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ExperienceBarLogic.INSTANCE.hookRenderExperienceBarLogic(minecraft, ci, graphics, left(minecraft.getWindow()), minecraft.font);
    }
}