package dev.dokko.ho.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public interface AccessorCPIM {

    @Invoker("syncSelectedSlot")
    void invokeSyncSelectedSlot();
}
