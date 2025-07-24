package dev.dokko.autoconfig.api.config.serialize;

import dev.dokko.autoconfig.api.config.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ConfigSerializer<T extends Config> {

    String serialize() throws Exception;

    void deserialize(String contents) throws Exception;

    String getFileName();

    default Path getFile(){
        return Path.of(FabricLoader.getInstance().getConfigDir().toString(), getFileName());
    }
    default String getContents(){
        try {
            if(!getFile().toFile().exists())return "";
            return Files.readString(getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
