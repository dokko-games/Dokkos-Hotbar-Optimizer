package dev.dokko.ho;

import dev.dokko.autoconfig.api.AutoConfig;
import dev.dokko.ho.config.HOConfig;
import dev.dokko.ho.mixin.AccessorCPIM;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DokkosHotbarOptimizer implements ClientModInitializer {
	public static final String MOD_ID = "dokkos-hotbar-optimizer";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HOConfig CONFIG;

	public static int lastSlot = -1;
	public static boolean sent = false;

	public static void syncItemState(MinecraftClient client, int slot) {
		if(sent && (!CONFIG.allowTickSwitches || ServerDatabase.shouldFlag)) return;
		if(!CONFIG.getItemsToSync().isIn(client.player.getInventory().getStack(slot).getItem())) return;
		client.player.getInventory().setSelectedSlot(slot);
		lastSlot = slot;
		if(CONFIG.isDebugMode())client.player.sendMessage(Text.literal("Syncing slot " + slot), true);
		// cast the interaction manager to custom accessor and invoke slot sync
		((AccessorCPIM) client.interactionManager).invokeSyncSelectedSlot();
		sent = true;

	}

	@Override
	public void onInitializeClient() {
		AutoConfig.register(HOConfig.class);
		AutoConfig.tryLoad(HOConfig.class);
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {sent = false;});
		LOGGER.info("Loaded Dokko's Hotbar Optimizer");
	}
}