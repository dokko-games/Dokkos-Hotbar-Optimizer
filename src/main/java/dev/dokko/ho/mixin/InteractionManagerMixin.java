package dev.dokko.ho.mixin;

import dev.dokko.ho.DokkosHotbarOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.dokko.ho.DokkosHotbarOptimizer.syncItemState;

@Mixin(ClientPlayerInteractionManager.class)
public class InteractionManagerMixin {

    @Inject(method = "clickSlot", at = @At("TAIL"))
    private void onInventoryClick(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        //same logic as KeybindingMixin
        MinecraftClient client = MinecraftClient.getInstance();

        if (!DokkosHotbarOptimizer.CONFIG.isEnabled()) return;
        if (client.player == null || client.interactionManager == null) return;
        if (client.player.isCreative()) return;

        int selectedSlot = client.player.getInventory().selectedSlot;
        ItemStack selectedStack = client.player.getInventory().getStack(selectedSlot);
        if (!selectedStack.isEmpty()) {
            syncItemState(client, selectedSlot);
        }
    }

}
