package dev.dokko.ho.mixin;

import dev.dokko.ho.DokkosHotbarOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
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

        if (!DokkosHotbarOptimizer.CONFIG.isEnabled()) return;
        if (client.player == null || client.interactionManager == null) return;
        if (client.player.isCreative()) return;

        int currentSlot = client.player.getInventory().selectedSlot;

        if (currentSlot != DokkosHotbarOptimizer.lastSlot) {
            DokkosHotbarOptimizer.lastSlot = currentSlot;

            ItemStack stack = client.player.getInventory().getStack(currentSlot);
            if (!stack.isEmpty()) {
                syncItemState(client, currentSlot);
            }
        }
    }

}
