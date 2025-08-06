package dev.dokko.ho.mixin;

import dev.dokko.ho.DokkosHotbarOptimizer;
import dev.dokko.ho.ServerDatabase;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.dokko.ho.DokkosHotbarOptimizer.syncItemState;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo info) {
        //same logic as KeybindingMixin
        MinecraftClient client = MinecraftClient.getInstance();

        if(client.isInSingleplayer() && !DokkosHotbarOptimizer.CONFIG.isDebugMode())return;

        if (!DokkosHotbarOptimizer.CONFIG.isEnabled() || ServerDatabase.shouldBlock) return;
        if (client.player == null || client.interactionManager == null) return;
        if (client.player.isInCreativeMode()) return;

        int currentSlot = client.player.getInventory().getSelectedSlot();

        if (currentSlot != DokkosHotbarOptimizer.lastSlot) {
            DokkosHotbarOptimizer.lastSlot = currentSlot;

            syncItemState(client, currentSlot);
        }
    }

}
