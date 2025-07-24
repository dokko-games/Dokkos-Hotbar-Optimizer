package dev.dokko.autoconfig.api.ui.element;

import dev.dokko.autoconfig.api.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EnumCyclerButton<E extends Enum<E>> implements UIElement {
    private final Config config;
    private final String key;
    private final Class<E> enumClass;
    private final E defaultValue;
    private final Function<Config, E> getter;
    private final BiConsumer<Config, E> setter;

    public EnumCyclerButton(Config config, String key, Class<E> enumClass, Function<Config, E> getter, BiConsumer<Config, E> setter) {
        this.config = config;
        this.key = key;
        this.enumClass = enumClass;
        this.getter = getter;
        this.setter = setter;
        this.defaultValue = getter.apply(config);
    }

    @Override
    public ClickableWidget build(int x, int y, int width, int height) {
        return ButtonWidget.builder(getMessage(), (button) -> {
                    E currentValue = getter.apply(config);

                    if (Screen.hasShiftDown()) {
                        setter.accept(config, defaultValue);
                        button.setMessage(getMessage());
                        return;
                    }

                    E[] values = enumClass.getEnumConstants();
                    int currentIndex = currentValue.ordinal();
                    int nextIndex = (currentIndex + 1) % values.length;

                    setter.accept(config, values[nextIndex]);
                    button.setMessage(getMessage());
                }).dimensions(x, y, width, height)
                .tooltip(Tooltip.of(Text.translatable(key + ".tooltip")))
                .build();
    }

    private Text getMessage() {
        E value = getter.apply(config);
        return Text.translatable(key)
                .append(": ")
                .append(Text.literal(formatEnum(value)).formatted(Formatting.AQUA));
    }

    private String formatEnum(E value) {
        // Default to capitalized enum name, override if you want translations
        return value.name().charAt(0) + value.name().substring(1).toLowerCase();
    }
}
