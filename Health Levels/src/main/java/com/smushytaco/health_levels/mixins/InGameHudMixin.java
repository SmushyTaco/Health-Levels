package com.smushytaco.health_levels.mixins;
import com.smushytaco.health_levels.mixin_logic.InGameHudLogic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;
    @Shadow
    public abstract TextRenderer getTextRenderer();
    @Shadow
    @Final
    private static Identifier ICONS;
    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void hookRenderExperienceBar(DrawContext context, int x, CallbackInfo ci) { InGameHudLogic.INSTANCE.hookRenderExperienceBarLogic(ICONS, client, ci, scaledHeight, context, x, scaledWidth, getTextRenderer()); }
}