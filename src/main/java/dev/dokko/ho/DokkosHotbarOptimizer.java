package dev.dokko.ho;

import dev.dokko.ho.config.HOConfig;
import dev.dokko.ho.mixin.AccessorCPIM;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


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
		client.player.sendMessage(Text.literal("syncing slot "+slot));
	}

	@Override
	public void onInitializeClient() {
		Path configFolder = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
		try {
			Files.createDirectories(configFolder);
		} catch (IOException e) {
			LOGGER.error("Failed to create config folder", e);
		}
		CONFIG = HOConfig.tryLoadFromFolder(configFolder);
		// In your mod init or a dedicated command registration class
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager.literal("hotbar-optimizer")
                        // Handle no argument: just print current status
                        .executes(context -> {
                            boolean enabled = CONFIG.isEnabled(); // Or CONFIG.getEnabled() depending on your config setup
                            context.getSource().sendFeedback(
                                    Text.literal("Hotbar Optimizer is currently " + (enabled ? "enabled" : "disabled"))
                                            .formatted(enabled ? Formatting.GREEN : Formatting.RED)
                            );
                            return 1;
                        })
                        // Handle the 'on' and 'off' arguments
                        .then(ClientCommandManager.literal("on")
                                .executes(context -> {
                                    CONFIG.setEnabled(true);
                                    context.getSource().sendFeedback(
                                            Text.literal("Hotbar Optimizer is now enabled")
                                                    .formatted(Formatting.GREEN)
                                    );
                                    CONFIG.saveToDefaultFolder();
                                    return 1;
                                }))
                        .then(ClientCommandManager.literal("off")
                                .executes(context -> {
                                    CONFIG.setEnabled(false);
                                    context.getSource().sendFeedback(
                                            Text.literal("Hotbar Optimizer is now disabled")
                                                    .formatted(Formatting.RED)
                                    );
                                    CONFIG.saveToDefaultFolder();
                                    return 1;
                                }))
        ));
		LOGGER.info("Loaded Dokko's Hotbar Optimizer");
	}
}