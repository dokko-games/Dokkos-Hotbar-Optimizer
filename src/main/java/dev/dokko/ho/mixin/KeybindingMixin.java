package dev.dokko.ho.mixin;

import dev.dokko.ho.DokkosHotbarOptimizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.dokko.ho.DokkosHotbarOptimizer.syncItemState;

@Mixin(KeyBinding.class)
public class KeybindingMixin {
	@Inject(at = @At("HEAD"), method = "setPressed")
	private void onPressed(boolean pressed, CallbackInfo info) {
		if(!pressed) return;
		// Return if the mod is disabled
		if(!DokkosHotbarOptimizer.CONFIG.isEnabled())return;
		// Get "this" as KeyBinding
		KeyBinding bind = (KeyBinding) (Object)this;
		// Get Minecraft.getMinecraft();
		MinecraftClient client = MinecraftClient.getInstance();
		// If the interaction manager is null, then we cannot sync the item state
		if(client.interactionManager == null)return;
		// Get mc.thePlayer
		ClientPlayerEntity player = client.player;
		// If there's no player, then we obviously cannot sync the item state
		if(player == null)return;
		// hotbar keys: key.hotbar.(1-9)
		if(bind.getTranslationKey().startsWith("key.hotbar.")){
			// slots go from 0 to 8, not from 1 to 9
			int slot = Integer.parseInt(bind.getTranslationKey().substring("key.hotbar.".length())) - 1;
			if(slot < 0 || slot > 8) return;
			// sync hotbar!
			syncItemState(client, slot);
		}
	}
}