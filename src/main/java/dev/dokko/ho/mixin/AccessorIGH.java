package dev.dokko.ho.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InGameHud.class)
public interface AccessorIGH {
    @Accessor("overlayRemaining")
    int getOverlayRemaining();
    @Accessor("overlayRemaining")
    void setOverlayRemaining(int remaining);
}
