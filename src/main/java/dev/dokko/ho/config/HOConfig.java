package dev.dokko.ho.config;

import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.config.ConfigSupplier;
import dev.dokko.autoconfig.api.config.annot.BooleanSetting;
import dev.dokko.autoconfig.api.config.annot.Setting;
import dev.dokko.autoconfig.api.config.serialize.ConfigSerializer;
import dev.dokko.autoconfig.api.config.serialize.impl.JsonSerializer;
import dev.dokko.autoconfig.api.setting.ItemList;
import dev.dokko.ho.DokkosHotbarOptimizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOConfig implements Config {


    @Setting
    @BooleanSetting(type = BooleanSetting.ONOFF)
    public boolean enabled = true;

    @Setting
    public ItemList itemsToSync = new ItemList(true);

    @Override
    public void init(ConfigSupplier<?> supplier) {
        DokkosHotbarOptimizer.CONFIG = this;
    }

    @Override
    public String getModId() {
        return "dho";
    }

    @Override
    public Class<? extends ConfigSerializer> getSerializer() {
        return JsonSerializer.class;
    }
}
