package dev.dokko.autoconfig.api.ui.element;

import dev.dokko.autoconfig.api.config.Config;
import dev.dokko.autoconfig.api.config.annot.BooleanSetting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class BooleanButton implements UIElement {
    private final Config config;
    private final String key;
    private final int type;
    private final boolean defaultValue;
    private final Function<Config, Boolean> getter;
    private final BiConsumer<Config, Boolean> setter;


    public BooleanButton(Config config, String key, int type, Function<Config, Boolean> getter, BiConsumer<Config, Boolean> setter) {
        this.config = config;
        this.key = key;
        this.type = type;
        this.defaultValue = getter.apply(config);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public ClickableWidget build(int x, int y, int width, int height) {
        return ButtonWidget.builder(getMessage(), (button) -> {
            if(Screen.hasShiftDown()){
                setter.accept(config, defaultValue);
                button.setMessage(getMessage());
                return;
            }
            setter.accept(config, !getter.apply(config));
            button.setMessage(getMessage());
        }).dimensions(x, y, width, height).tooltip(Tooltip.of(Text.translatable(key+".tooltip"))).build();
    }

    private Text getMessage() {
        return Text.translatable(key).append(": ").append(getValueText());
    }

    private Text getValueText() {
        boolean value = getter.apply(config);
        if(type == BooleanSetting.YESNO){
            if(value){
                return Text.literal("YES").formatted(Formatting.GREEN);
            }
            return Text.literal("NO").formatted(Formatting.RED);
        } else if(type == BooleanSetting.ONOFF){
            if(value){
                return Text.literal("ON").formatted(Formatting.GREEN);
            }
            return Text.literal("OFF").formatted(Formatting.RED);
        } else if(type == BooleanSetting.TRUEFALSE){
            if(value){
                return Text.literal("TRUE").formatted(Formatting.GREEN);
            }
            return Text.literal("FALSE").formatted(Formatting.RED);
        }
        return Text.empty();
    }
}
