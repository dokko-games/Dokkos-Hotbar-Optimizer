package dev.dokko.ho.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.dokko.autoconfig.api.AutoConfig;
import dev.dokko.ho.config.HOConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getSupplier(HOConfig.class).build(parent);
    }
}
