package dev.dokko.ho;

import dev.dokko.ho.config.HOConfig;
import dev.dokko.ho.mixin.AccessorCPIM;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DokkosHotbarOptimizer implements ClientModInitializer {
	public static final String MOD_ID = "dokkos-hotbar-optimizer";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static HOConfig CONFIG;

	public static int lastSlot = -1;

	public static void syncItemState(MinecraftClient client, int slot) {
		if(CONFIG.isOnlySyncTotem() && !client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING))return;
		client.player.getInventory().selectedSlot = slot;
		lastSlot = slot;
		// cast the interaction manager to custom accessor and invoke slot sync
		((AccessorCPIM) client.interactionManager).invokeSyncSelectedSlot();
	}

	@Override
	public void onInitializeClient() {
		HOConfig.init();
		LOGGER.info("Loaded Dokko's Hotbar Optimizer");
	}
}