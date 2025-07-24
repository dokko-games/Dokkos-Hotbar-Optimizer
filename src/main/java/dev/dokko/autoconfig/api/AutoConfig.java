package dev.dokko.autoconfig.api;

import dev.dokko.autoconfig.AutoConfigAPI;
import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.config.ConfigSupplier;
import dev.dokko.autoconfig.api.config.serialize.ConfigSerializer;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class AutoConfig {
    private static final Map<Class<? extends Config>, ConfigSupplier<? extends Config>> registeredConfigurations;

    static {
        registeredConfigurations = new HashMap<>();
    }

    public static <T extends Config> void register(Class<T> configClass){
        try {
            T config = configClass.getConstructor().newInstance();
            ConfigSupplier<T> supplier = new ConfigSupplier<>(config);
            try {
                configClass.getMethod("init", ConfigSupplier.class).invoke(config, supplier);
                registeredConfigurations.put(configClass, supplier);
            }catch (Exception e){
                AutoConfigAPI.LOGGER.error("Error! Cannot instantiate {} because it does not have the method init(ConfigSupplier)", configClass.getName());
            }
        }catch (Exception e){
            AutoConfigAPI.LOGGER.error("Error! Cannot instantiate {} because a public empty constructor does not exist", configClass.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Config> ConfigSupplier<T> getSupplier(Class<T> configClass) {
        if(!registeredConfigurations.containsKey(configClass)){
            throw new RuntimeException(configClass.getName()+" not registered");
        }
        return (ConfigSupplier<T>) registeredConfigurations.get(configClass);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Config> T getConfig(Class<T> configClass) {
        if(!registeredConfigurations.containsKey(configClass)){
            throw new RuntimeException(configClass.getName()+" not registered");
        }
        return (T) registeredConfigurations.get(configClass).getConfig();
    }
    @SuppressWarnings("unchecked")
    public static <T extends Config> void tryLoad(Class<T> configClass) {
        if(!registeredConfigurations.containsKey(configClass)){
            throw new RuntimeException(configClass.getName()+" not registered");
        }
        try {
            T config = getConfig(configClass);
            ConfigSerializer<T> serializer = (ConfigSerializer<T>) config.getSerializer().getDeclaredConstructor(Config.class).newInstance(config);
            serializer.deserialize(serializer.getContents());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Config> void saveConfig(Class<T> configClass) {
        if(!registeredConfigurations.containsKey(configClass)){
            throw new RuntimeException(configClass.getName()+" not registered");
        }
        try {
            T config = getConfig(configClass);
            ConfigSerializer<T> serializer = (ConfigSerializer<T>) config.getSerializer().getDeclaredConstructor(Config.class).newInstance(config);
            String serialized = serializer.serialize();
            Files.writeString(serializer.getFile(), serialized);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
