package dev.dokko.ho.mixin;

import dev.dokko.ho.DokkosHotbarOptimizer;
import dev.dokko.ho.ServerDatabase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class CPNHMixin {

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onWorldJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null) {
            if (ServerDatabase.shouldBlock && DokkosHotbarOptimizer.CONFIG.isEnabled()) {
                client.inGameHud.setOverlayMessage(
                        Text.translatable("text.dho.block").formatted(Formatting.RED), false
                );
                ((AccessorIGH)client.inGameHud).setOverlayRemaining(120);
            } else if (ServerDatabase.shouldFlag && DokkosHotbarOptimizer.CONFIG.isAllowTickSwitches()) {
                client.inGameHud.setOverlayMessage(
                        Text.translatable("text.dho.flag").formatted(Formatting.RED), false
                );
                ((AccessorIGH)client.inGameHud).setOverlayRemaining(120);
            }
        }
    }
}
