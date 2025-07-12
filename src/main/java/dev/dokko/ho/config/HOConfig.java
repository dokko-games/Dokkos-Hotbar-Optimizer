package dev.dokko.ho.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dokko.ho.DokkosHotbarOptimizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HOConfig {
    private static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
    private boolean enabled = true;
    public static HOConfig tryLoadFromFolder(Path configFolder) {
        Path primary = configFolder.resolve(DokkosHotbarOptimizer.MOD_ID +".json");

        if (Files.exists(primary)) {
            try {
                return load(primary);
            } catch (Exception e) {
                DokkosHotbarOptimizer.LOGGER.error("Failed to load config from {}", primary, e);
            }
        }
        return new HOConfig();
    }
    private static HOConfig load(Path path) throws IOException {
        String serialized = Files.readString(path);
        return HOConfig.PRETTY.fromJson(serialized, HOConfig.class);
    }
    public void saveToDefaultFolder() {
        Path configFolder = FabricLoader.getInstance().getConfigDir().resolve(DokkosHotbarOptimizer.MOD_ID);
        this.saveToFolder(configFolder);
    }
    public synchronized void saveToFolder(Path configFolder) {
        Path primary = configFolder.resolve(DokkosHotbarOptimizer.MOD_ID +".json");
        this.save(primary);
    }
    private void save(Path path) {
        String serialized = PRETTY.toJson(this, HOConfig.class);

        try {
            Files.writeString(path, serialized, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE, StandardOpenOption.DSYNC);
        } catch (IOException e) {
            DokkosHotbarOptimizer.LOGGER.error("Failed to save config", e);
        }
    }
}
