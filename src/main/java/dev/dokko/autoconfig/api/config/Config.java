package dev.dokko.autoconfig.api.config;

import dev.dokko.autoconfig.api.config.serialize.ConfigSerializer;

public interface Config {
    void init(ConfigSupplier<?> supplier);
    String getModId();
    Class<? extends ConfigSerializer> getSerializer();
}
