package dev.dokko.ho;

import dev.dokko.ho.config.HOConfig;
import dev.dokko.ho.mixin.AccessorCPIM;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class DokkosHotbarOptimizer implements ClientModInitializer {
	public static final String MOD_ID = "dokkos-hotbar-optimizer";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HOConfig CONFIG;

	public static int lastSlot = -1;

	public static void syncItemState(MinecraftClient client, int slot) {
		client.player.getInventory().selectedSlot = slot;
		lastSlot = slot;
		// cast the interaction manager to custom accessor and invoke slot sync
		((AccessorCPIM) client.interactionManager).invokeSyncSelectedSlot();
	}

	@Override
	public void onInitializeClient() {
		HOConfig.init();
		// In your mod init or a dedicated command registration class
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("hotbar-optimizer")
                        // Handle no argument: just print current status
                        .executes(context -> {
                            boolean enabled = CONFIG.isEnabled();
                            context.getSource().sendFeedback(
                                    Text.literal("Hotbar Optimizer is currently ")
											.append(Text.literal( enabled ? "enabled" : "disabled")
                                            .formatted(enabled ? Formatting.GREEN : Formatting.RED))
                            );
                            return 1;
                        })
                        // Handle the 'on' and 'off' arguments
                        .then(ClientCommandManager.literal("on")
                                .executes(context -> {
                                    CONFIG.setEnabled(true);
                                    context.getSource().sendFeedback(
											Text.literal("Hotbar Optimizer is now ")
													.append(Text.literal("enabled")
															.formatted(Formatting.GREEN))
                                    );
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("off")
                                .executes(context -> {
                                    CONFIG.setEnabled(false);
                                    context.getSource().sendFeedback(
											Text.literal("Hotbar Optimizer is now ")
													.append(Text.literal("disabled")
															.formatted(Formatting.RED))
                                    );
                                    return 1;
                                }))
        ));
		LOGGER.info("Loaded Dokko's Hotbar Optimizer");
	}
}