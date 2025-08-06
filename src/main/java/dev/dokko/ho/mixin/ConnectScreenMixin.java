package dev.dokko.ho.mixin;

import dev.dokko.ho.ServerDatabase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "init", at = @At("HEAD"))
    private void onConnectScreenInit(CallbackInfo ci) {
        ServerDatabase.reset(); // reset flags on each new connection

        MinecraftClient client = MinecraftClient.getInstance();
        ServerInfo entry = client.getCurrentServerEntry();

        if (entry != null) {
            String address = entry.address;

            for (String blocked : ServerDatabase.serversThatBlock) {
                if (address.contains(blocked)) {
                    ServerDatabase.shouldBlock = true;
                    break;
                }
            }

            for (String flagged : ServerDatabase.serversThatFlag) {
                if (address.contains(flagged)) {
                    ServerDatabase.shouldFlag = true;
                    break;
                }
            }
        }
    }
}
