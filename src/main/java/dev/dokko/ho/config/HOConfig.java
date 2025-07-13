package dev.dokko.ho.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.dokko.ho.DokkosHotbarOptimizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Config(name = "dho")
public class HOConfig implements ConfigData {

    public static void init()
    {
        AutoConfig.register(HOConfig.class, JanksonConfigSerializer::new);
        DokkosHotbarOptimizer.CONFIG = AutoConfig.getConfigHolder(HOConfig.class).getConfig();
    }

    @ConfigEntry.Gui.Excluded
    private static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();

    @ConfigEntry.Gui.Tooltip()
    private boolean enabled = true;
}
