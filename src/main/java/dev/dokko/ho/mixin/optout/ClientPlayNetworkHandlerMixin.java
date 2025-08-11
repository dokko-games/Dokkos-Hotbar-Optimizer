package dev.dokko.ho.mixin.optout;

import dev.dokko.ho.DokkosHotbarOptimizer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        ClientPlayNetworkHandler networkHandler = (ClientPlayNetworkHandler)(Object) this;
        Identifier id = Identifier.of(DokkosHotbarOptimizer.MOD_ID, "optout");
        UnknownCustomPayload payload = new UnknownCustomPayload(id);
        networkHandler.sendPacket(new CustomPayloadC2SPacket(payload));
    }
}
