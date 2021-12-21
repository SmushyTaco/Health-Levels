package com.smushytaco.health_levels.mixins;
import com.smushytaco.health_levels.mixin_logic.InGameHudLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    @Shadow
    public abstract TextRenderer getTextRenderer();
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void hookRenderExperienceBar(MatrixStack matrices, int x, CallbackInfo ci) {
        InGameHudLogic.INSTANCE.hookRenderExperienceBarLogic(this, client, ci, scaledHeight, matrices, x, scaledWidth, getTextRenderer());
    }
}